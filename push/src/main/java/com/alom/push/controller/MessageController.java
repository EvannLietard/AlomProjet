package com.alom.push.controller;

import com.alom.push.model.MessageRequest;
import com.alom.push.tcp.TcpAuthServer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private TcpAuthServer tcpAuthServer;

    /**
     * Envoie un message à un client connecté
     * @param messageRequest Le corps de la requête contenant le nickname et le message
     * @return ResponseEntity avec le statut de l'envoi
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@Valid @RequestBody MessageRequest messageRequest) {
        Map<String, Object> response = new HashMap<>();

        if (!tcpAuthServer.isClientConnected(messageRequest.getNickname())) {
            response.put("success", false);
            response.put("message", "Le client '" + messageRequest.getNickname() + "' n'est pas connecté");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        boolean sent = tcpAuthServer.sendMessageToClient(
            messageRequest.getNickname(),
            messageRequest.getMessage()
        );

        if (sent) {
            response.put("success", true);
            response.put("message", "Message envoyé avec succès à " + messageRequest.getNickname());
            response.put("recipient", messageRequest.getNickname());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Échec de l'envoi du message");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}