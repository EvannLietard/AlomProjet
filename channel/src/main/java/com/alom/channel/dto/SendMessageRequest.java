package com.alom.channel.dto;

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
    
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String content;
    
    @NotBlank(message = "L'expéditeur est obligatoire")
    private String senderNickname;
}
