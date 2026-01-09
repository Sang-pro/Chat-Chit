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
public class UserOauthRequest {
    @NotBlank(message = "clientId is required")
    private String clientId;
    
    @NotBlank(message = "idToken is required")
    private String idToken;
}
