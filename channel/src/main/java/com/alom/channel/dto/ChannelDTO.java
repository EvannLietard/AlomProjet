package com.alom.channel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {
    
    private String id;
    
    @NotBlank(message = "Le nom du channel est obligatoire")
    private String name;
    
    @Builder.Default
    private Set<String> subscribers = new HashSet<>();
    
    private Integer subscriberCount;
}
