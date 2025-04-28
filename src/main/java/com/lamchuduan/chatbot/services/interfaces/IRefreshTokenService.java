package com.lamchuduan.chatbot.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.lamchuduan.chatbot.entities.RefreshToken;
import com.lamchuduan.chatbot.entities.User;

public interface IRefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken useToken(RefreshToken token, String replacedByToken);
    void revokeToken(RefreshToken token, String reason);
    void deleteByUser(User user);
    List<RefreshToken> findActiveTokensByUser(User user);
    void purgeExpiredTokens();
}
