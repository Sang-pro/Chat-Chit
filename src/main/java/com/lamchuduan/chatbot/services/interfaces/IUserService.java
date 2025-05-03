package com.lamchuduan.chatbot.services.interfaces;

import java.util.UUID;

import com.lamchuduan.chatbot.dtos.requests.ChangePasswordRequest;
import com.lamchuduan.chatbot.dtos.requests.UpdateProfileRequest;
import com.lamchuduan.chatbot.dtos.responses.MessageResponse;
import com.lamchuduan.chatbot.dtos.responses.UserProfileResponse;
import com.lamchuduan.chatbot.entities.User;

public interface IUserService {
    User findByUsername (String username);
    User findById (UUID id);

    UserProfileResponse getUserProfile (String username);
    UserProfileResponse updateProfile(String username, UpdateProfileRequest request);

    MessageResponse changePassword(String username, ChangePasswordRequest request);
    MessageResponse updateAvatar(String username, String avatarUrl);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
