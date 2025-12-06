package com.alom.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @NotBlank(message = "Sender nickname is required")
    private String senderNickname;
    
    @NotBlank(message = "Receiver nickname is required")
    private String receiverNickname;
    
    @NotBlank(message = "Content is required")
    private String content;
}
