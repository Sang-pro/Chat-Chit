package com.lamchuduan.chatbot.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevokeTokenRequest {
    @NotBlank(message = "Token is required")
    private String token;
    private String reason;
}
