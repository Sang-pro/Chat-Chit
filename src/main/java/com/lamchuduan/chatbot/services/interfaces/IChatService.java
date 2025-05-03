package com.lamchuduan.chatbot.services.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lamchuduan.chatbot.dtos.requests.ChatRequest;
import com.lamchuduan.chatbot.dtos.requests.MessageRequest;
import com.lamchuduan.chatbot.dtos.responses.ChatMessageResponse;
import com.lamchuduan.chatbot.dtos.responses.ChatResponse;

public interface IChatService {
    ChatResponse createChat(String username, ChatRequest request);
    ChatResponse getChat(String username, UUID chatId);

    Page<ChatResponse> getUserChats(String username, Pageable pageable);

    void deleteChat(String username, UUID chatId);
    ChatMessageResponse sendMessage(String username, UUID chatId, MessageRequest request);

    List<ChatMessageResponse> getChatMessages(String username, UUID chatId);
}
