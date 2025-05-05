package com.lamchuduan.chatbot.services.implementations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lamchuduan.chatbot.dtos.ollama.OllamaApiResponses;
import com.lamchuduan.chatbot.dtos.ollama.OllamaModelDetails;
import com.lamchuduan.chatbot.dtos.ollama.OllamaModelResponse;
import com.lamchuduan.chatbot.services.interfaces.IOllamaModelService;

import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaModelService implements IOllamaModelService {
    private final WebClient ollamaWebClient;
    private static final int DEAFULT_TIMEOUT_SECONDS = 30;

    @Override
    public List<OllamaModelResponse> listModels() {
        try {
            OllamaApiResponses response = ollamaWebClient.get()
                    .uri("/tags")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(OllamaApiResponses.class)
                    .block(Duration.ofSeconds(DEAFULT_TIMEOUT_SECONDS));
            if (response != null && response.getModels() != null) {
                return response.getModels();
            }
        } catch (Exception e) {
            log.error("Error retrieving models from Ollama API: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<OllamaModelResponse> listRunningModels() {
        try {
            OllamaApiResponses response = ollamaWebClient.get()
                    .uri("/running")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(OllamaApiResponses.class)
                    .block(Duration.ofSeconds(DEAFULT_TIMEOUT_SECONDS));
            if (response != null && response.getModels() != null) {
                return response.getModels();
            }
        } catch (Exception e) {
            log.error("Failed to list running models: {}", e != null ? e.toString() : "Unknown error");
        }
        return Collections.emptyList();
    }

    @Override
    public OllamaModelDetails getModelDetails(String model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getModelDetails'");
    }
    @Override
    public boolean copyModel(String source, String destination) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'copyModel'");
    }
    @Override
    public boolean deleteModel(String model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteModel'");
    }
    @Override
    public String pullModel(String model, boolean insecure, boolean stream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pullModel'");
    }
    @Override
    public String pushModel(String model, boolean insecure, boolean stream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pushModel'");
    }


}
