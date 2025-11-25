package com.alom.channel.dto;

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
public class ChannelMessageDTO {
    
    @NotBlank(message = "L'ID du channel est obligatoire")
    private String channelId;
    
    @NotBlank(message = "Le nom du channel est obligatoire")
    private String channelName;
    
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String content;
    
    @NotBlank(message = "L'expéditeur est obligatoire")
    private String sender;
    
    private LocalDateTime timestamp;
}
