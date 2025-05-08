package com.lamchuduan.chatbot.services.interfaces;

import java.util.List;

import com.lamchuduan.chatbot.dtos.ollama.OllamaModelDetails;
import com.lamchuduan.chatbot.dtos.ollama.OllamaModelResponse;

public interface IOllamaModelService {

    List<OllamaModelResponse> listLocalModels();
    List<OllamaModelResponse> listRunningModels();

    OllamaModelDetails getModelDetails(String model);
    boolean copyModel(String source, String destination);
    boolean deleteModel(String model);

    String pullModel(String model, boolean insecure, boolean stream);
    String pushModel(String model, boolean insecure, boolean stream);
}
