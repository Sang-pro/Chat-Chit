package com.lamchuduan.chatbot.controllers;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@Tag(name = "API Documentation", description = "API documentation endpoints")
public class ApiDocsController {

    @GetMapping("/api-docs")
    public Map<String, Object> getApiInfo() {

        Map<String, Object> apiInfo = Map.of(
            "title", "Chatbot API Documentation",
            "version", "1.0.0",
            "description", "This API provides endpoints for interacting with the chatbot system."
        );

        
        return apiInfo;
    }
    
}
