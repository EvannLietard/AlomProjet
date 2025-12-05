package com.alom.push.dto;

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
    private String channelId;
    private String channelName;
    private String senderId;
    private String content;
    private LocalDateTime timestamp;
}
