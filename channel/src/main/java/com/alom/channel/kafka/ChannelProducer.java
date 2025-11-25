package com.alom.channel.kafka;

import com.alom.channel.dto.ChannelMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelProducer {
    
    private final KafkaTemplate<String, ChannelMessageDTO> messageKafkaTemplate;
    
    private static final String USER_TOPIC_PREFIX = "user-messages-";
    
    /**
     * Envoie un message à tous les utilisateurs abonnés au channel
     * Le message est envoyé dans le topic individuel de chaque utilisateur
     */
    public void sendMessageToChannelSubscribers(ChannelMessageDTO message, Set<String> subscriberIds) {
        log.info("Envoi d'un message du channel {} à {} abonnés", 
                message.getChannelName(), subscriberIds.size());
        
        subscriberIds.forEach(userId -> {
            String userTopic = USER_TOPIC_PREFIX + userId;
            log.debug("Envoi du message au topic: {}", userTopic);
            
            CompletableFuture<SendResult<String, ChannelMessageDTO>> future = 
                    messageKafkaTemplate.send(userTopic, userId, message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Message envoyé avec succès à l'utilisateur {} - Offset: {}", 
                            userId, result.getRecordMetadata().offset());
                } else {
                    log.error("Erreur lors de l'envoi du message à l'utilisateur {}", userId, ex);
                }
            });
        });
        
        log.info("Message du channel {} envoyé à tous les abonnés", message.getChannelName());
    }
}
