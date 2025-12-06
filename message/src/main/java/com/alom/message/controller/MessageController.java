package com.alom.message.controller;

import com.alom.message.dto.MessageDTO;
import com.alom.message.dto.MessageListResponse;
import com.alom.message.dto.SendMessageRequest;
import com.alom.message.entity.Message;
import com.alom.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<MessageDTO> messages = messageService.getMessagesByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(messages);
    }
}
