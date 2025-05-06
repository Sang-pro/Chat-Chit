package com.lamchuduan.chatbot.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lamchuduan.chatbot.entities.RefreshToken;
import com.lamchuduan.chatbot.entities.User;
import com.lamchuduan.chatbot.services.interfaces.IRefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {@Override
    public Optional<RefreshToken> findByToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByToken'");
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createRefreshToken'");
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyExpiration'");
    }

    @Override
    public RefreshToken useToken(RefreshToken token, String replacedByToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'useToken'");
    }

    @Override
    public void revokeToken(RefreshToken token, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'revokeToken'");
    }

    @Override
    public void deleteByUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUser'");
    }

    @Override
    public List<RefreshToken> findActiveTokensByUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findActiveTokensByUser'");
    }

    @Override
    public void purgeExpiredTokens() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'purgeExpiredTokens'");
    }

}
