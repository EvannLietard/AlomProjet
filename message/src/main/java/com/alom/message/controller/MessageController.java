package com.alom.message.controller;

import com.alom.message.dto.MessageDTO;
import com.alom.message.dto.MessageListResponse;
import com.alom.message.dto.SendMessageRequest;
import com.alom.message.entity.Message;
import com.alom.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * Envoie un message dans le topic Kafka de l'utilisateur
     * POST /api/messages
     */
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("Requête reçue pour envoyer un message de {} à {}", request.getSenderId(), request.getReceiverId());
        
        // Créer le MessageDTO avec userId = receiverId (celui qui va recevoir)
        MessageDTO messageDTO = MessageDTO.builder()
                .userId(request.getReceiverId())
                .sender(request.getSenderId())
                .content(request.getContent())
                .build();
        
        MessageDTO sentMessage = messageService.sendMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
    }

    /**
     * Récupère tous les messages d'un utilisateur
     * GET /api/messages/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<MessageListResponse> getMessagesByUserId(@PathVariable String userId) {
        log.info("Requête reçue pour récupérer les messages de l'utilisateur: {}", userId);
        MessageListResponse response = messageService.getMessagesByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les messages d'un utilisateur avec un statut spécifique
     * GET /api/messages/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<MessageDTO>> getMessagesByUserIdAndStatus(
            @PathVariable String userId,
            @PathVariable Message.MessageStatus status) {
        log.info("Requête reçue pour récupérer les messages avec le statut {} de l'utilisateur: {}", 
                status, userId);
        List<MessageDTO> messages = messageService.getMessagesByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(messages);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Message service is running");
    }
}
