package com.alom.push.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    /**
     * Enregistre un token et son nickname associé
     */
    public void registerToken(String token, String nickname) {
        tokenStore.put(token, nickname);
        System.out.println("Token enregistré : " + token + " -> " + nickname);
    }

    /**
     * Récupère le nickname associé à un token
     */
    public String getNickname(String token) {
        return tokenStore.get(token);
    }

    /**
     * Vérifie si un token existe
     */
    public boolean isValidToken(String token) {
        return tokenStore.containsKey(token);
    }
}