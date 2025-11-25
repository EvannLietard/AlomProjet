package com.alom.channel.service;

import com.alom.channel.dto.ChannelDTO;

import java.util.List;

public interface ChannelService {
    
    /**
     * Crée un nouveau channel
     */
    ChannelDTO createChannel(ChannelDTO channelDTO);
    
    /**
     * Récupère un channel par son ID
     */
    ChannelDTO getChannelById(String id);
    
    /**
     * Récupère un channel par son nom
     */
    ChannelDTO getChannelByName(String name);
    
    /**
     * Récupère tous les channels
     */
    List<ChannelDTO> getAllChannels();
    
    
    /**
     * Abonne un utilisateur à un channel
     */
    ChannelDTO subscribeUser(String channelId, String userId);
    
    /**
     * Désabonne un utilisateur d'un channel
     */
    ChannelDTO unsubscribeUser(String channelId, String userId);
    
    /**
     * Récupère tous les channels auxquels un utilisateur est abonné
     */
    List<ChannelDTO> getChannelsBySubscriber(String userId);
    
    /**
     * Envoie un message à tous les abonnés d'un channel
     */
    void sendMessageToChannel(String channelId, String content, String sender);
}
