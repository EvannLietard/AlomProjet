package com.alom.push.controller;

import com.alom.push.model.TokenRegistration;
import com.alom.push.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    /**
     * Endpoint pour enregistrer un token
     * POST /api/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerToken(@RequestBody TokenRegistration registration) {
        
        if (registration.getToken() == null || registration.getToken().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Token is required"));
        }
        
        if (registration.getNickname() == null || registration.getNickname().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Nickname is required"));
        }

        tokenService.registerToken(registration.getToken(), registration.getNickname());
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Token registered for " + registration.getNickname());
        
        return ResponseEntity.ok(response);
    }
}