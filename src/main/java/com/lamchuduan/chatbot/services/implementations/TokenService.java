package com.lamchuduan.chatbot.services.implementations;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.lamchuduan.chatbot.security.UserDetailsImpl;
import com.lamchuduan.chatbot.services.interfaces.ITokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService implements ITokenService {@Override
    public String generateAccessToken(UserDetailsImpl userPrincipal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateAccessToken'");
    }

    @Override
    public String generateRefreshToken(UserDetailsImpl userPrincipal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateRefreshToken'");
    }

    @Override
    public String getUsernameFromToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsernameFromToken'");
    }

    @Override
    public boolean validateToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateToken'");
    }

    @Override
    public Authentication getAuthentication(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthentication'");
    }
    

}
