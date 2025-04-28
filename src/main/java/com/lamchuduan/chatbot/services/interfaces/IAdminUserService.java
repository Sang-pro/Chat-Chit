package com.lamchuduan.chatbot.services.interfaces;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lamchuduan.chatbot.dtos.admin.UserAdminResponse;
import com.lamchuduan.chatbot.dtos.admin.UserStatusUpdateRequest;
import com.lamchuduan.chatbot.dtos.responses.MessageResponse;

public interface IAdminUserService {
    Page<UserAdminResponse> findAllUser(String search, Boolean isActive, Pageable pageable);
    UserAdminResponse getUserDetails(UUID userid);
    MessageResponse updateUserStatus(UUID userId, UserStatusUpdateRequest request);
}
