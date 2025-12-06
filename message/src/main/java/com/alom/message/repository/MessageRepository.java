package com.alom.message.repository;

import com.alom.message.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    /**
     * Récupère tous les messages d'un utilisateur
     */
    List<Message> findByReceiverNicknameOrderByTimestampDesc(String receiverNickname);
    
    /**
     * Récupère les messages d'un utilisateur avec un statut spécifique
     */
    List<Message> findByReceiverNicknameAndStatus(String receiverNickname, Message.MessageStatus status);
    
    /**
     * Compte le nombre de messages d'un utilisateur
     */
    long countByReceiverNickname(String receiverNickname);
}
