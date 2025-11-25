package com.alom.channel.mapper;

import com.alom.channel.dto.ChannelDTO;
import com.alom.channel.entity.Channel;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {
    
    public ChannelDTO toDTO(Channel channel) {
        if (channel == null) {
            return null;
        }
        
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .subscribers(channel.getSubscribers())
                .subscriberCount(channel.getSubscribers() != null ? channel.getSubscribers().size() : 0)
                .build();
    }
    
    public Channel toEntity(ChannelDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Channel.builder()
                .id(dto.getId())
                .name(dto.getName())
                .subscribers(dto.getSubscribers())
                .build();
    }
}
