package com.lamchuduan.chatbot.dtos.responses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.lamchuduan.chatbot.entities.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private UUID id;
    private Message role;
    private String content;
    private Integer tokens;
    private String model;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", timezone = "UTC")
    private String createdAt;
}
