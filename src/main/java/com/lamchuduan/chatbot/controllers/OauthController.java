package com.lamchuduan.chatbot.controllers;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lamchuduan.chatbot.dtos.requests.UserOauthRequest;
import com.lamchuduan.chatbot.dtos.responses.JwtResponse;
import com.lamchuduan.chatbot.events.AuthenticationEvent;
import com.lamchuduan.chatbot.exceptions.AccountDeactivatedException;
import com.lamchuduan.chatbot.services.interfaces.IAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
@Tag(name = "OAuth2", description = "OAuth2 Authentication API endpoints")
public class OauthController {
    private final IAuthService authService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/google")
    @Operation(
        summary = "Authenticate user via Google OAuth",
        description = "Authenticate user using Google OAuth token (idToken), returns JWT token",
        responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
                ),
                @ApiResponse(
                    responseCode = "401", 
                    description = "Invalid username or password"
                )
        }
    )
    public ResponseEntity<JwtResponse> oauth2LoginGoogle(
            @Valid @RequestBody UserOauthRequest loginRequest, HttpServletRequest httpRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            eventPublisher.publishEvent(new AuthenticationEvent(
                    this, 
                    loginRequest.getIdToken(), 
                    AuthenticationEvent.AuthEventType.LOGIN_SUCCESS,
                    "Login successful",
                    getClientIp(httpRequest)
            ));
            return ResponseEntity.ok(jwtResponse);
        } catch (AccountDeactivatedException e) {
            eventPublisher.publishEvent(new AuthenticationEvent(
                    this, 
                    loginRequest.getIdToken(), 
                    AuthenticationEvent.AuthEventType.LOGIN_FAILED,
                    "Account deactivated",
                    getClientIp(httpRequest)
            ));
            throw e;
        } catch (BadCredentialsException e) {
            eventPublisher.publishEvent(new AuthenticationEvent(
                    this, 
                    loginRequest.getIdToken(), 
                    AuthenticationEvent.AuthEventType.LOGIN_FAILED,
                    "Invalid credentials",
                    getClientIp(httpRequest)
            ));
            throw e;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}