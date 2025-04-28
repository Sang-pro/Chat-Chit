package com.lamchuduan.chatbot.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lamchuduan.chatbot.entities.Chat;
import com.lamchuduan.chatbot.entities.User;

@Repository
public interface IChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findByUserOrderByUpdatedAtDesc(UUID userId);

    Page<Chat> findByUser(User user, Pageable pageable);

    Optional<Chat> findByIdAndUser(UUID id, User user);

    Page<Chat> findByUserId(UUID id, Pageable pageable);

    @Query("SELECT c FROM Chat c WHERE c.user.id = :userId")
    Integer countByUserId(@Param("userId") UUID userId);
    
}
