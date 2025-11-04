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
        authService.put("path", "/api/auth/**");
        authService.put("port", "8081");
        authService.put("description", "Gestion des utilisateurs et authentification");
        services.put("auth", authService);
        
        Map<String, String> messageService = new HashMap<>();
        messageService.put("name", "Message Service");
        messageService.put("path", "/api/messages/**");
        messageService.put("port", "8082");
        messageService.put("description", "Gestion des messages");
        services.put("messages", messageService);
        
        Map<String, String> channelService = new HashMap<>();
        channelService.put("name", "Channel Service");
        channelService.put("path", "/api/channels/**");
        channelService.put("port", "8083");
        channelService.put("description", "Gestion des channels");
        services.put("channels", channelService);
        
        Map<String, String> tcpService = new HashMap<>();
        tcpService.put("name", "TCP Service");
        tcpService.put("path", "/api/tcp/**");
        tcpService.put("port", "8084");
        tcpService.put("description", "Interface TCP pour communication retour");
        services.put("tcp", tcpService);
        
        response.put("services", services);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }
}
