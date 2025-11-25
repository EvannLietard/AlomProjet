package com.alom.push.service;

import com.alom.push.client.AuthClient;
import com.alom.push.dto.TokenValidationRequestDTO;
import com.alom.push.dto.TokenValidationResponseDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    
    private final AuthClient authClient;

    /**
     * Vérifie si un token est valide
     * @param token le token d'authentification
     * @return true si le token est valide, false sinon
     */
    public TokenValidationResponseDTO isTokenValid(String token) {
        try {
            TokenValidationRequestDTO request = new TokenValidationRequestDTO(token);
            return this.authClient.validateToken(request);
        } catch (FeignException e) {
            log.error("Erreur lors de la validation du token: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la validation du token: {}", e.getMessage(), e);
            return null;
        }
    }
}
