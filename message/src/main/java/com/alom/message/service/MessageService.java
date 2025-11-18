package com.alom.message.service;

import com.alom.message.dto.MessageDTO;
import com.alom.message.dto.MessageListResponse;
import com.alom.message.entity.Message;

import java.util.List;

public interface MessageService {
    
    /**
     * Envoie un message dans le topic Kafka de l'utilisateur
     */
    MessageDTO sendMessage(MessageDTO messageDTO);
    
    /**
     * Récupère tous les messages d'un utilisateur
     */
    MessageListResponse getMessagesByUserId(String userId);
    
    /**
     * Crée un topic Kafka pour un utilisateur s'il n'existe pas déjà
     */
    void createUserTopicIfNotExists(String userId);
    
    /**
     * Récupère les messages d'un utilisateur avec un statut spécifique
     */
    List<MessageDTO> getMessagesByUserIdAndStatus(String userId, Message.MessageStatus status);
}
