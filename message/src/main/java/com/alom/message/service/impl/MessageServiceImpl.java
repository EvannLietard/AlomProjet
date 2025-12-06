package com.alom.message.service.impl;

import com.alom.message.dto.MessageDTO;
import com.alom.message.dto.MessageListResponse;
import com.alom.message.entity.Message;
import com.alom.message.repository.MessageRepository;
import com.alom.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;
    private final MessageRepository messageRepository;

    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO) {
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
                .receiverNickname(messageDTO.getReceiverNickname())
                .content(messageDTO.getContent())
                .senderNickname(messageDTO.getSenderNickname())
                .timestamp(messageDTO.getTimestamp())
                .status(messageDTO.getStatus())
                .build();
        
        Message savedMessage = messageRepository.save(message);
        
        // Mettre à jour le DTO avec l'ID généré
        messageDTO.setId(savedMessage.getId());
        
        // Nom du topic spécifique à l'utilisateur
        // Le topic sera créé automatiquement par Kafka lors du premier envoi
        String topicName = "user-messages-" + messageDTO.getReceiverNickname();
        
        // Envoi du message dans Kafka
        kafkaTemplate.send(topicName, messageDTO.getReceiverNickname(), messageDTO);
        
        return messageDTO;
    }

    @Override
    public MessageListResponse getMessagesByUserId(String userId) {
        List<Message> messages = messageRepository.findByReceiverNicknameOrderByTimestampDesc(userId);
        long totalCount = messageRepository.countByReceiverNickname(userId);
        
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
    }

    @Override
    public List<MessageDTO> getMessagesByUserIdAndStatus(String userId, Message.MessageStatus status) {
        List<Message> messages = messageRepository.findByReceiverNicknameAndStatus(userId, status);
        
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
                .receiverNickname(message.getReceiverNickname())
                .content(message.getContent())
                .senderNickname(message.getSenderNickname())
                .timestamp(message.getTimestamp())
                .status(message.getStatus())
                .build();
    }
}
