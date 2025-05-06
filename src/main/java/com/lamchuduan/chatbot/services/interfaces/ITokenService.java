package com.lamchuduan.chatbot.services.interfaces;

import org.springframework.security.core.Authentication;

import com.lamchuduan.chatbot.security.UserDetailsImpl;

public interface ITokenService {
    String generateAccessToken(UserDetailsImpl userPrincipal);
    String generateRefreshToken(UserDetailsImpl userPrincipal);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
}
