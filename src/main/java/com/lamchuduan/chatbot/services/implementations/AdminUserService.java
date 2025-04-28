package com.lamchuduan.chatbot.services.implementations;

import java.util.UUID;

import com.lamchuduan.chatbot.dtos.admin.UserAdminResponse;
import com.lamchuduan.chatbot.dtos.admin.UserStatusUpdateRequest;
import com.lamchuduan.chatbot.dtos.responses.MessageResponse;
import com.lamchuduan.chatbot.entities.Role;
import com.lamchuduan.chatbot.entities.User;
import com.lamchuduan.chatbot.exceptions.ResourceNotFoundException;
import com.lamchuduan.chatbot.exceptions.ServiceException;
import com.lamchuduan.chatbot.repositories.IChatRepository;
import com.lamchuduan.chatbot.repositories.IMessageRepository;
import com.lamchuduan.chatbot.repositories.IUserRepository;
import com.lamchuduan.chatbot.services.interfaces.IAdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService implements IAdminUserService{
    private final IUserRepository userRepository;
    private final IMessageRepository messageRepository;
    private final IChatRepository chatRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserAdminResponse> findAllUser(String search, Boolean isActive, Pageable pageable) {
        log.info("Searching for all users with search: {}, isActive: {}, page = {}", search, isActive, pageable);
        Page<User> users;
        if (search != null && !search.isBlank()) {
            users = userRepository.findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
                                    search, search, search, search, pageable);
        }
        else if (isActive != null) {
            users = userRepository.findByIsActive(isActive, pageable);
        }
        else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::mapToUserAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAdminResponse getUserDetails(UUID userid) {
        log.info("Getting user details for userId: {}", userid);
        User user = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return mapToUserAdminResponse(user);
    }

    @Override
    @Transactional
    public MessageResponse updateUserStatus(UUID userId, UserStatusUpdateRequest request) {
        log.info("Updating user status for userId: {}, request: {}", userId, request);
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (hasAdminRole && Boolean.FALSE.equals(request.getIsActive())) {
                throw new ServiceException("Khong thể vô hiệu hóa tài khoản admin");
            }
            if (request.getIsActive() != null) {
                user.setActive(request.getIsActive()); // Có lỗi ở đây.
            }

            if (request.getLockedUntil() != null) {
                user.setLockedUntil(request.getLockedUntil());
            }

            userRepository.save(user);

            String statusMessage = Boolean.TRUE.equals(request.getIsActive()) ? "Kích hoạt" : "Vô hiệu hóa";
            return MessageResponse.builder()
                    .message(statusMessage)
                    .success(true)
                    .build();
        } 
        catch (ResourceNotFoundException e) {
            throw e;
        } 
        catch (Exception e) {
            log.error("Lỗi khi cập nhập trạng thái", e);
            throw new ServiceException("Lỗi khi cập nhập trạng thái người dùng", e); //Có lỗi ở đây.
        }
    }

    private UserAdminResponse mapToUserAdminResponse(User user) {
        Integer chatCount = chatRepository.countByUserId(user.getId());
        Integer messageCount = messageRepository.countByUser(user.getId());
        return UserAdminResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .displayName(user.getDisplayName())
                                .phoneNumber(user.getPhoneNumber())
                                .isActive(user.isActive()) // Có lỗi ở đây.
                                .lockedUntil(user.getLockedUntil())
                                .roles(user.getRoles().stream().map(Role::getName).toList())
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getUpdatedAt())
                                .chatCount(chatCount)
                                .messageCount(messageCount)
                                .build();
    }
}
