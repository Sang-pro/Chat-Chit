package com.lamchuduan.chatbot.services.implementations;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.lamchuduan.chatbot.dtos.facebook.FacebookDebugTokenResponse;
import com.lamchuduan.chatbot.dtos.facebook.FacebookUserResponse;
import com.lamchuduan.chatbot.dtos.requests.LoginRequest;
import com.lamchuduan.chatbot.dtos.requests.LogoutRequest;
import com.lamchuduan.chatbot.dtos.requests.RefreshTokenRequest;
import com.lamchuduan.chatbot.dtos.requests.RegisterRequest;
import com.lamchuduan.chatbot.dtos.requests.UserOauthRequest;
import com.lamchuduan.chatbot.dtos.responses.JwtResponse;
import com.lamchuduan.chatbot.dtos.responses.MessageResponse;
import com.lamchuduan.chatbot.entities.RefreshToken;
import com.lamchuduan.chatbot.entities.Role;
import com.lamchuduan.chatbot.entities.User;
import com.lamchuduan.chatbot.events.AuthProvider;
import com.lamchuduan.chatbot.exceptions.AccountDeactivatedException;
import com.lamchuduan.chatbot.exceptions.TokenRefreshException;
import com.lamchuduan.chatbot.repositories.IRoleRepository;
import com.lamchuduan.chatbot.repositories.IUserRepository;
import com.lamchuduan.chatbot.security.UserDetailsImpl;
import com.lamchuduan.chatbot.services.interfaces.IAuthService;
import com.lamchuduan.chatbot.services.interfaces.IRefreshTokenService;
import com.lamchuduan.chatbot.services.interfaces.ITokenService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final ITokenService tokenService;
    private final IRefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<AuthenticationManager> authenticationManagerProvider;
    private final GoogleIdTokenVerifier googleVerifier;
    private final WebClient webClient;
    private final String facebookClientId;
    private final String facebookClientSecret;

    public AuthService(
            IUserRepository userRepository,
            IRoleRepository roleRepository,
            ITokenService tokenService,
            IRefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            ObjectProvider<AuthenticationManager> authenticationManagerProvider,
            WebClient.Builder webClientBuilder,
            @Value("${spring.security.oauth2.client.registration.google.client-id}") String googleClientId,
            @Value("${spring.security.oauth2.client.registration.facebook.client-id}") String facebookClientId,
            @Value("${spring.security.oauth2.client.registration.facebook.client-secret}") String facebookClientSecret) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerProvider = authenticationManagerProvider;
        this.webClient = webClientBuilder.baseUrl("https://graph.facebook.com").build();
        this.facebookClientId = facebookClientId;
        this.facebookClientSecret = facebookClientSecret;

        // Initialize Google token verifier
        this.googleVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Override
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        AuthenticationManager authenticationManager = authenticationManagerProvider.getObject();
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));
        
        if (!user.isActive()) {
            throw new AccountDeactivatedException("User account is deactivated");
        }

        return generateJwtResponse(userDetails);
    }

    @Override
    @Transactional
    public JwtResponse authenticateUser(UserOauthRequest loginRequest) {
        User user = switch (loginRequest.getProvider()) {
            case GOOGLE -> {
                log.info("Starting Google OAuth authentication for token: {}", loginRequest.getToken());
                GoogleIdToken idToken = verifyGoogleToken(loginRequest.getToken());
                if (idToken == null) {
                    log.warn("Invalid Google ID Token received");
                    throw new BadCredentialsException("Invalid Google ID Token");
                }
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                log.info("Google OAuth payload email: {}", email);
                yield userRepository.findByEmail(email)
                        .orElseGet(() -> createNewGoogleUser(payload));
            }
            case FACEBOOK -> {
                log.info("Starting Facebook OAuth authentication for token: {}", loginRequest.getToken());
                FacebookUserResponse facebookUser = verifyFacebookToken(loginRequest.getToken());
                if (facebookUser == null) {
                    log.warn("Invalid Facebook Access Token received");
                    throw new BadCredentialsException("Invalid Facebook Access Token");
                }
                String email = facebookUser.getEmail();
                log.info("Facebook OAuth payload email: {}", email);
                yield userRepository.findByEmail(email)
                        .orElseGet(() -> createNewFacebookUser(facebookUser));
            }
            default -> throw new BadCredentialsException("Unsupported OAuth provider");
        };

        log.info("User authenticated: {} (ID: {})", user.getUsername(), user.getId());

        if (!user.isActive()) {
            log.warn("User account is deactivated: {}", user.getUsername());
            throw new AccountDeactivatedException("User account is deactivated");
        }

        // Manually create authentication for the SecurityContext
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Generating JWT response for user: {}", user.getUsername());
        return generateJwtResponse(userDetails);
    }

    private GoogleIdToken verifyGoogleToken(String token) {
        try {
            return googleVerifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google ID token", e);
            return null;
        }
    }

    private FacebookUserResponse verifyFacebookToken(String token) {
        // Step 1: Verify the token is valid and belongs to our app
        String appAccessToken = facebookClientId + "|" + facebookClientSecret;
        FacebookDebugTokenResponse debugResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/debug_token")
                        .queryParam("input_token", token)
                        .queryParam("access_token", appAccessToken)
                        .build())
                .retrieve()
                .bodyToMono(FacebookDebugTokenResponse.class)
                .doOnError(e -> log.error("Error debugging Facebook token", e))
                .onErrorResume(e -> Mono.empty()) // Handle errors by returning an empty Mono
                .block();

        if (debugResponse == null || !debugResponse.getData().isValid() || !debugResponse.getData().getAppId().equals(facebookClientId)) {
            log.warn("Invalid Facebook token or token does not belong to our app. App ID from token: {}", 
                     debugResponse != null && debugResponse.getData() != null ? debugResponse.getData().getAppId() : "N/A");
            return null;
        }

        // Step 2: Fetch the user's profile
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me")
                        .queryParam("fields", "id,email,first_name,last_name,picture.type(large)")
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(FacebookUserResponse.class)
                .doOnError(e -> log.error("Error fetching user profile from Facebook", e))
                .onErrorResume(e -> Mono.empty()) // Handle errors by returning an empty Mono
                .block();
    }
    
    private User createNewGoogleUser(Payload payload) {
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String avatarUrl = (String) payload.get("picture");

        // Create a unique username as it's a required field. Use email by default.
        String username = email;
        if (userRepository.existsByUsername(username)) {
            // If email-based username exists, append a short UUID to make it unique
            username = username + "_" + UUID.randomUUID().toString().substring(0, 4);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default role 'ROLE_USER' not found."));

        User newUser = User.builder()
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .avatarUrl(avatarUrl)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Set a random, unusable password
                .provider(AuthProvider.GOOGLE)
                .isActive(true)
                .roles(Collections.singleton(userRole))
                .build();

        log.info("Creating new user from Google OAuth: {}", email);
        return userRepository.save(newUser);
    }

    private User createNewFacebookUser(FacebookUserResponse facebookUser) {
        String email = facebookUser.getEmail();
        if (email == null || email.isBlank()) {
            // Handle case where Facebook user does not provide an email
            // You might want to throw an exception or create a user without an email
            // For now, we'll use the facebook ID to create a placeholder email
            email = facebookUser.getId() + "@facebook.com";
        }

        // Create a unique username
        String username = email;
        if (userRepository.existsByUsername(username)) {
            username = username + "_" + UUID.randomUUID().toString().substring(0, 4);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default role 'ROLE_USER' not found."));

        String avatarUrl = (facebookUser.getPicture() != null && facebookUser.getPicture().getData() != null)
                ? facebookUser.getPicture().getData().getUrl() : null;

        User newUser = User.builder()
                .email(email)
                .username(username)
                .firstName(facebookUser.getFirstName())
                .lastName(facebookUser.getLastName())
                .avatarUrl(avatarUrl)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password
                .provider(AuthProvider.FACEBOOK)
                .isActive(true)
                .roles(Collections.singleton(userRole))
                .build();

        log.info("Creating new user from Facebook OAuth: {}", email);
        return userRepository.save(newUser);
    }
    
    private JwtResponse generateJwtResponse(UserDetailsImpl userDetails) {
        String accessToken = tokenService.generateAccessToken(userDetails);
        
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userDetails.getId()));
        
        RefreshToken refreshToken;
        try {
            refreshToken = refreshTokenService.createRefreshToken(user);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Constraint violation creating refresh token for user '{}', checking for existing tokens.", user.getUsername());
            List<RefreshToken> activeTokens = refreshTokenService.findActiveTokensByUser(user);
            if (activeTokens.isEmpty()) {
                throw new RuntimeException("Could not create or find a valid refresh token for the user.");
            }
            refreshToken = activeTokens.get(0);
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    User user = refreshToken.getUser();
                    UserDetailsImpl userDetails = UserDetailsImpl.build(user);
                    
                    String accessToken = tokenService.generateAccessToken(userDetails);
                    RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();
                    
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .id(userDetails.getId())
                            .username(userDetails.getUsername())
                            .email(userDetails.getEmail())
                            .roles(roles)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found in database"));
    }

    @Override
    @Transactional
    public MessageResponse registerUser(RegisterRequest registerRequest) {
        if (existsByEmail(registerRequest.getEmail())) {
            return MessageResponse.builder()
                    .message("Email already in use")
                    .success(false)
                    .build();
        }

        if (existsByUsername(registerRequest.getUsername())) {
            return MessageResponse.builder()
                    .message("Username already in use")
                    .success(false)
                    .build();
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .provider(AuthProvider.LOCAL)
                .build();

        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } 
        else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName("ROLE_USER")
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        return MessageResponse.builder()
                .message("User registered successfully")
                .success(true)
                .build();
    }

    @Override
    public MessageResponse logoutUser(LogoutRequest logoutRequest) {
        if (logoutRequest != null && logoutRequest.getUsername() != null) {
            userRepository.findByUsername(logoutRequest.getUsername())
                    .ifPresent(refreshTokenService::deleteByUser);
        }
        
        return MessageResponse.builder()
                .message("Logout successful")
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse revokeToken(String token, String reason) {
        return refreshTokenService.findByToken(token).map(refreshToken -> {
            if (refreshToken.isRevoked()) {
                return MessageResponse.builder()
                        .message("Token already revoked")
                        .success(false)
                        .build();
            }

            String finalReason = (reason != null && !reason.isBlank()) ? reason : "Manually revoked by user";
            refreshTokenService.revokeToken(refreshToken, finalReason);

            log.info("Token revoked for user: {}, reason: {}", refreshToken.getUser().getUsername(), finalReason);
            return MessageResponse.builder()
                    .message("Token revoked successfully")
                    .success(true)
                    .build();
        })
        .orElse(MessageResponse.builder()
                        .message("Token not found")
                        .success(false)
                        .build());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}