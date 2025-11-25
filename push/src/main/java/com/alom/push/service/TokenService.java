package com.alom.push.service;

import com.alom.push.client.AuthClient;
import com.alom.push.dto.TokenValidationRequestDTO;
import com.alom.push.dto.TokenValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final AuthClient authClient;

    /**
     * Récupère le nickname associé à un token
     * @param token le token d'authentification
     * @return le nickname si le token est valide, null sinon
     */
    public String getNickname(String token) {
        try {
            TokenValidationRequestDTO request = new TokenValidationRequestDTO(token);
            TokenValidationResponseDTO response = authClient.getUserByToken(request);
            return response != null ? response.getNickname() : null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du token : " + e.getMessage());
            return null;
        }
    }

    /**
     * Vérifie si un token est valide
     * @param token le token d'authentification
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token) {
        try {
            TokenValidationRequestDTO request = new TokenValidationRequestDTO(token);
            Boolean isValid = authClient.validateToken(request);
            return isValid != null && isValid;
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du token : " + e.getMessage());
            return false;
        }
    }
}

