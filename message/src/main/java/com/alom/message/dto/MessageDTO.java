package com.alom.message.dto;

import com.alom.message.entity.Message.MessageStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    
    private String id;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotBlank(message = "Sender is required")
    private String sender;
    
    private LocalDateTime timestamp;
    private MessageStatus status;
}
