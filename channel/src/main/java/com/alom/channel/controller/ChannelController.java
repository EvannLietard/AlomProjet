package com.alom.channel.controller;

import com.alom.channel.dto.ChannelDTO;
import com.alom.channel.dto.SendMessageRequest;
import com.alom.channel.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelService channelService;
    
    /**
     * Crée un nouveau channel
     * POST /api/channels
     */
    @PostMapping
    public ResponseEntity<ChannelDTO> createChannel(@Valid @RequestBody ChannelDTO channelDTO) {
        ChannelDTO createdChannel = channelService.createChannel(channelDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }
    
    /**
     * Récupère un channel par son ID
     * GET /api/channels/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChannelDTO> getChannelById(@PathVariable String id) {
        ChannelDTO channel = channelService.getChannelById(id);
        return ResponseEntity.ok(channel);
    }
    
    /**
     * Récupère un channel par son nom
     * GET /api/channels/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ChannelDTO> getChannelByName(@PathVariable String name) {
        ChannelDTO channel = channelService.getChannelByName(name);
        return ResponseEntity.ok(channel);
    }
    
    /**
     * Récupère tous les channels
     * GET /api/channels
     */
    @GetMapping
    public ResponseEntity<List<ChannelDTO>> getAllChannels() {
        List<ChannelDTO> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }
    
    
    /**
     * Abonne un utilisateur à un channel
     * POST /api/channels/{channelId}/subscribe/{userId}
     */
    @PostMapping("/{channelId}/subscribe/{userId}")
    public ResponseEntity<ChannelDTO> subscribeUser(
            @PathVariable String channelId,
            @PathVariable String userId) {
        ChannelDTO channel = channelService.subscribeUser(channelId, userId);
        return ResponseEntity.ok(channel);
    }
    
    /**
     * Désabonne un utilisateur d'un channel
     * POST /api/channels/{channelId}/unsubscribe/{userId}
     */
    @PostMapping("/{channelId}/unsubscribe/{userId}")
    public ResponseEntity<ChannelDTO> unsubscribeUser(
            @PathVariable String channelId,
            @PathVariable String userId) {
        ChannelDTO channel = channelService.unsubscribeUser(channelId, userId);
        return ResponseEntity.ok(channel);
    }
    
    /**
     * Récupère tous les channels auxquels un utilisateur est abonné
     * GET /api/channels/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChannelDTO>> getChannelsBySubscriber(@PathVariable String userId) {
        List<ChannelDTO> channels = channelService.getChannelsBySubscriber(userId);
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Envoie un message à tous les abonnés d'un channel
     * POST /api/channels/{channelId}/messages
     */
    @PostMapping("/{channelId}/messages")
    public ResponseEntity<Void> sendMessageToChannel(
            @PathVariable String channelId,
            @Valid @RequestBody SendMessageRequest request) {
        channelService.sendMessageToChannel(channelId, request.getContent(), request.getSender());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
