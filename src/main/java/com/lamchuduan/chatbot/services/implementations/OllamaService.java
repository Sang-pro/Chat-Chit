package com.lamchuduan.chatbot.services.implementations;

import java.util.List;
import java.util.Map;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.lamchuduan.chatbot.dtos.ollama.OllamaCompletionResponse;
import com.lamchuduan.chatbot.services.interfaces.IOllamaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j  
@Service
@RequiredArgsConstructor
public class OllamaService implements IOllamaService{@Override
    public OllamaCompletionResponse genderateCompletion(String model, 
                                                        List<Map<String, String>> messages,
                                                        Map<String, Object> options) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'genderateCompletion'");
    }

    @Override
    public Flux<ServerSentEvent<Object>> streamCompletion(String model, 
                                                          List<Map<String, String>> messages,
                                                          Map<String, Object> options, 
                                                          boolean streaming) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamCompletion'");
    }
    
}
