package com.alom.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur principal de la Gateway
 */
@RestController
@RequestMapping("/")
public class GatewayController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Alom Gateway");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", Instant.now().toString());
        
        Map<String, Map<String, String>> services = new HashMap<>();
        
        Map<String, String> authService = new HashMap<>();
        authService.put("name", "Authentication Service");
        authService.put("path", "/auth/**");
        authService.put("port", "8081");
        authService.put("description", "Gestion des utilisateurs et authentification");
        services.put("auth", authService);
        
        Map<String, String> messageService = new HashMap<>();
        messageService.put("name", "Message Service");
        messageService.put("path", "/messages/**");
        messageService.put("port", "8082");
        messageService.put("description", "Gestion des messages");
        services.put("messages", messageService);
        
        Map<String, String> channelService = new HashMap<>();
        channelService.put("name", "Channel Service");
        channelService.put("path", "/channels/**");
        channelService.put("port", "8083");
        channelService.put("description", "Gestion des channels");
        services.put("channels", channelService);
        
        Map<String, String> pushService = new HashMap<>();
        pushService.put("name", "Push Service");
        pushService.put("path", "/push/**");
        pushService.put("port", "8084");
        pushService.put("description", "Service de notifications push");
        services.put("push", pushService);
        
        response.put("services", services);
        
        return ResponseEntity.ok(response);
    }
}
