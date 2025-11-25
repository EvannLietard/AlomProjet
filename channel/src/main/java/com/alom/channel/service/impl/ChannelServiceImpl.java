package com.alom.channel.service.impl;

import com.alom.channel.dto.ChannelDTO;
import com.alom.channel.dto.ChannelMessageDTO;
import com.alom.channel.entity.Channel;
import com.alom.channel.kafka.ChannelProducer;
import com.alom.channel.mapper.ChannelMapper;
import com.alom.channel.repository.ChannelRepository;
import com.alom.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final ChannelProducer channelProducer;
    
    @Override
    public ChannelDTO createChannel(ChannelDTO channelDTO) {
        log.info("Création du channel: {}", channelDTO.getName());
        
        // Vérifier si le channel existe déjà
        if (channelRepository.existsByName(channelDTO.getName())) {
            throw new IllegalArgumentException("Un channel avec ce nom existe déjà");
        }
        
        Channel channel = channelMapper.toEntity(channelDTO);
        Channel savedChannel = channelRepository.save(channel);
        
        log.info("Channel créé avec succès: {}", savedChannel.getId());
        return channelMapper.toDTO(savedChannel);
    }
    
    @Override
    public ChannelDTO getChannelById(String id) {
        log.info("Récupération du channel avec l'ID: {}", id);
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + id));
        return channelMapper.toDTO(channel);
    }
    
    @Override
    public ChannelDTO getChannelByName(String name) {
        log.info("Récupération du channel avec le nom: {}", name);
        Channel channel = channelRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec le nom: " + name));
        return channelMapper.toDTO(channel);
    }
    
    @Override
    public List<ChannelDTO> getAllChannels() {
        log.info("Récupération de tous les channels");
        return channelRepository.findAll().stream()
                .map(channelMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    
    @Override
    public ChannelDTO subscribeUser(String channelId, String userId) {
        log.info("Abonnement de l'utilisateur {} au channel {}", userId, channelId);
        
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + channelId));
        
        channel.addSubscriber(userId);
        Channel savedChannel = channelRepository.save(channel);
        
        log.info("Utilisateur {} abonné avec succès au channel {}", userId, channelId);
        return channelMapper.toDTO(savedChannel);
    }
    
    @Override
    public ChannelDTO unsubscribeUser(String channelId, String userId) {
        log.info("Désabonnement de l'utilisateur {} du channel {}", userId, channelId);
        
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + channelId));
        
        channel.removeSubscriber(userId);
        Channel savedChannel = channelRepository.save(channel);
        
        log.info("Utilisateur {} désabonné avec succès du channel {}", userId, channelId);
        return channelMapper.toDTO(savedChannel);
    }
    
    @Override
    public List<ChannelDTO> getChannelsBySubscriber(String userId) {
        log.info("Récupération des channels pour l'utilisateur: {}", userId);
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getSubscribers() != null && 
                                 channel.getSubscribers().contains(userId))
                .map(channelMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void sendMessageToChannel(String channelId, String content, String sender) {
        log.info("Envoi d'un message au channel: {}", channelId);
        
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel non trouvé avec l'ID: " + channelId));
        
        if (channel.getSubscribers() == null || channel.getSubscribers().isEmpty()) {
            log.warn("Aucun abonné pour le channel: {}", channelId);
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
        log.info("Message envoyé à {} abonnés du channel {}", channel.getSubscribers().size(), channel.getName());
    }
}
