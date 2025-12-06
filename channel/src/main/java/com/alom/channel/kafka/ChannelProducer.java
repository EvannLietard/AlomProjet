package com.alom.channel.kafka;

import com.alom.channel.dto.ChannelMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

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
        subscriberIds.forEach(userId -> {
            String userTopic = USER_TOPIC_PREFIX + userId;
            messageKafkaTemplate.send(userTopic, userId, message);
        });
    }
}
