package com.lamchuduan.chatbot.controllers;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lamchuduan.chatbot.dtos.requests.RevokeTokenRequest;
import com.lamchuduan.chatbot.dtos.responses.MessageResponse;
import com.lamchuduan.chatbot.events.AuthenticationEvent;
import com.lamchuduan.chatbot.repositories.IRefreshTokenRepository;
import com.lamchuduan.chatbot.repositories.IUserRepository;
import com.lamchuduan.chatbot.services.interfaces.IAdminUserService;
import com.lamchuduan.chatbot.services.interfaces.IAuthService;
import com.lamchuduan.chatbot.services.interfaces.IRefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Admin" , description = "Admin APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    
    private final IAuthService authService;
    private final IRefreshTokenService refreshTokenService;
    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final IAdminUserService adminUserService;

    @PostMapping("/tokens/revoke")
    @Operation(
            summary = "Revoke refresh token (Admin)",
            description = "Admin endpoint to invalidate any refresh token",
            responses = {
                @ApiResponse(
                            responseCode = "200",
                            description = "Refresh tokens revoked successfully",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
                @ApiResponse(
                            responseCode = "400",
                            description = "Invalid token or token not found"),
                @ApiResponse(
                            responseCode = "403", 
                            description = "Access denied"),
        }
    )
    public ResponseEntity<MessageResponse> revokeToken(@Valid @RequestBody RevokeTokenRequest request, HttpServletRequest httpRequest) {
        String adminReason = "Admin revocation: " + (request.getReason() != null ? request.getReason() : "No reason provided");
        MessageResponse response = authService.revokeToken(request.getToken(), adminReason);

        if (response.isSuccess()) {
            eventPublisher.publishEvent(new AuthenticationEvent(
                    this,
                    "admin-action",
                    AuthenticationEvent.AuthEventType.INVALID_TOKEN,
                    "Admin revoked token: " + adminReason,
                    getClientIp(httpRequest)
            ));
        }
        return ResponseEntity.ok(response);
    }

    

    private String getClientIp(HttpServletRequest httpRequest) {
        String xfHeader = httpRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return httpRequest.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
