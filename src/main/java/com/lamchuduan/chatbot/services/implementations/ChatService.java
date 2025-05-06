package com.lamchuduan.chatbot.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lamchuduan.chatbot.dtos.requests.ChatRequest;
import com.lamchuduan.chatbot.dtos.requests.MessageRequest;
import com.lamchuduan.chatbot.dtos.responses.ChatMessageResponse;
import com.lamchuduan.chatbot.dtos.responses.ChatResponse;
import com.lamchuduan.chatbot.services.interfaces.IChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements IChatService {@Override
    public ChatResponse createChat(String username, ChatRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createChat'");
    }

    @Override
    public ChatResponse getChat(String username, UUID chatId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChat'");
    }

    @Override
    public Page<ChatResponse> getUserChats(String username, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserChats'");
    }

    @Override
    public void deleteChat(String username, UUID chatId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteChat'");
    }

    @Override
    public ChatMessageResponse sendMessage(String username, UUID chatId, MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }

    @Override
    public List<ChatMessageResponse> getChatMessages(String username, UUID chatId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChatMessages'");
    }
    
}
