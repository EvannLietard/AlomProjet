package com.alom.message.kafka;

import com.alom.message.dto.MessageDTO;
import com.alom.message.entity.Message;
import com.alom.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final MessageRepository messageRepository;

    /**
     * Écoute tous les topics des utilisateurs avec un pattern
     * Pattern: user-messages-* pour écouter tous les topics d'utilisateurs
     */
    @KafkaListener(
        id = "messageListenerContainer",
        topicPattern = "user-messages-.*",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMessage(
            @Payload MessageDTO messageDTO,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        log.info("Message reçu du topic {}: {}", topic, messageDTO);
        
        try {
            // Création de l'entité Message à partir du DTO
            Message message = Message.builder()
                    .userId(messageDTO.getUserId())
                    .content(messageDTO.getContent())
                    .sender(messageDTO.getSender())
                    .timestamp(messageDTO.getTimestamp() != null ? messageDTO.getTimestamp() : LocalDateTime.now())
                    .status(messageDTO.getStatus() != null ? messageDTO.getStatus() : Message.MessageStatus.DELIVERED)
                    .build();
            
            // Sauvegarde dans MongoDB
            Message savedMessage = messageRepository.save(message);
            log.info("Message sauvegardé avec l'ID: {}", savedMessage.getId());
            
        } catch (Exception e) {
            log.error("Erreur lors du traitement du message: {}", e.getMessage(), e);
        }
    }
}
