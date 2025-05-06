package com.lamchuduan.chatbot.services.interfaces;

import com.lamchuduan.chatbot.dtos.requests.LoginRequest;
import com.lamchuduan.chatbot.dtos.requests.LogoutRequest;
import com.lamchuduan.chatbot.dtos.requests.RefreshTokenRequest;
import com.lamchuduan.chatbot.dtos.requests.RegisterRequest;
import com.lamchuduan.chatbot.dtos.responses.JwtResponse;
import com.lamchuduan.chatbot.dtos.responses.MessageResponse;

public interface IAuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    MessageResponse registerUser(RegisterRequest registerRequest);
    MessageResponse logoutUser(LogoutRequest logoutRequest);
    MessageResponse revokeToken(String token, String reason);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
