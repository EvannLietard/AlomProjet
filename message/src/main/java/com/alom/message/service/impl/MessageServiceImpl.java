package com.alom.message.service.impl;

import com.alom.message.dto.MessageDTO;
import com.alom.message.dto.MessageListResponse;
import com.alom.message.entity.Message;
import com.alom.message.repository.MessageRepository;
import com.alom.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;
    private final MessageRepository messageRepository;

    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        log.info("Envoi du message pour l'utilisateur: {}", messageDTO.getUserId());
        
        // Définir le timestamp si non défini
        if (messageDTO.getTimestamp() == null) {
            messageDTO.setTimestamp(LocalDateTime.now());
        }
        
        // Définir le statut par défaut si non défini
        if (messageDTO.getStatus() == null) {
            messageDTO.setStatus(Message.MessageStatus.SENT);
        }
        
        // Sauvegarder le message dans MongoDB
        Message message = Message.builder()
                .userId(messageDTO.getUserId())
                .content(messageDTO.getContent())
                .sender(messageDTO.getSender())
                .timestamp(messageDTO.getTimestamp())
                .status(messageDTO.getStatus())
                .build();
        
        Message savedMessage = messageRepository.save(message);
        log.info("Message sauvegardé dans MongoDB avec l'ID: {}", savedMessage.getId());
        
        // Mettre à jour le DTO avec l'ID généré
        messageDTO.setId(savedMessage.getId());
        
        // Nom du topic spécifique à l'utilisateur
        // Le topic sera créé automatiquement par Kafka lors du premier envoi
        String topicName = "user-messages-" + messageDTO.getUserId();
        
        // Envoi du message dans Kafka
        kafkaTemplate.send(topicName, messageDTO.getUserId(), messageDTO)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message envoyé avec succès au topic {}: offset={}", 
                                topicName, result.getRecordMetadata().offset());
                    } else {
                        log.error("Erreur lors de l'envoi du message au topic {}: {}", 
                                topicName, ex.getMessage());
                    }
                });
        
        return messageDTO;
    }

    @Override
    public MessageListResponse getMessagesByUserId(String userId) {
        log.info("Récupération des messages pour l'utilisateur: {}", userId);
        
        List<Message> messages = messageRepository.findByUserIdOrderByTimestampDesc(userId);
        long totalCount = messageRepository.countByUserId(userId);
        
        List<MessageDTO> messageDTOs = messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return MessageListResponse.builder()
                .messages(messageDTOs)
                .totalCount(totalCount)
                .build();
    }

    @Override
    public void createUserTopicIfNotExists(String userId) {
        // Cette méthode n'est plus nécessaire avec l'auto-création de Kafka
        // Le topic sera créé automatiquement lors du premier envoi de message
        log.debug("Topic user-messages-{} sera créé automatiquement par Kafka si nécessaire", userId);
    }

    @Override
    public List<MessageDTO> getMessagesByUserIdAndStatus(String userId, Message.MessageStatus status) {
        log.info("Récupération des messages avec le statut {} pour l'utilisateur: {}", status, userId);
        
        List<Message> messages = messageRepository.findByUserIdAndStatus(userId, status);
        
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Message en MessageDTO
     */
    private MessageDTO convertToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .userId(message.getUserId())
                .content(message.getContent())
                .sender(message.getSender())
                .timestamp(message.getTimestamp())
                .status(message.getStatus())
                .build();
    }
}
