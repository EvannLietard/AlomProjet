package com.alom.channel.service.impl;

import com.alom.channel.dto.ChannelDTO;
import com.alom.channel.dto.ChannelMessageDTO;
import com.alom.channel.entity.Channel;
import com.alom.channel.kafka.ChannelProducer;
import com.alom.channel.mapper.ChannelMapper;
import com.alom.channel.repository.ChannelRepository;
import com.alom.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final ChannelProducer channelProducer;
    
    @Override
    public ChannelDTO createChannel(ChannelDTO channelDTO) {
        // Vérifier si le channel existe déjà
        if (channelRepository.existsByName(channelDTO.getName())) {
            throw new IllegalArgumentException("Un channel avec ce nom existe déjà");
        }
        
        Channel channel = channelMapper.toEntity(channelDTO);
        Channel savedChannel = channelRepository.save(channel);
        
        return channelMapper.toDTO(savedChannel);
    }
    
    @Override
    public ChannelDTO getChannelById(String id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + id));
        return channelMapper.toDTO(channel);
    }
    
    @Override
    public ChannelDTO getChannelByName(String name) {
        Channel channel = channelRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec le nom: " + name));
        return channelMapper.toDTO(channel);
    }
    
    @Override
    public List<ChannelDTO> getAllChannels() {
        return channelRepository.findAll().stream()
                .map(channelMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    
    @Override
    public ChannelDTO subscribeUser(String channelId, String userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + channelId));
        
        channel.addSubscriber(userId);
        Channel savedChannel = channelRepository.save(channel);
        
        return channelMapper.toDTO(savedChannel);
    }
    
    @Override
    public ChannelDTO unsubscribeUser(String channelId, String userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + channelId));
        
        channel.removeSubscriber(userId);
        Channel savedChannel = channelRepository.save(channel);
        
        return channelMapper.toDTO(savedChannel);
    }
    
    @Override
    public List<ChannelDTO> getChannelsBySubscriber(String userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getSubscribers() != null && 
                                 channel.getSubscribers().contains(userId))
                .map(channelMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void sendMessageToChannel(String channelId, String content, String sender) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + channelId));
        
        if (channel.getSubscribers() == null || channel.getSubscribers().isEmpty()) {
            return;
        }
        
        ChannelMessageDTO message = ChannelMessageDTO.builder()
                .channelId(channel.getId())
                .channelName(channel.getName())
                .content(content)
                .sender(sender)
                .timestamp(LocalDateTime.now())
                .build();
        
        channelProducer.sendMessageToChannelSubscribers(message, channel.getSubscribers());
    }
}
