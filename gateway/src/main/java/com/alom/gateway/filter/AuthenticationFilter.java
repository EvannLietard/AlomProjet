package com.alom.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Component
@Order(2)
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (isAuthRequired(path)) {
            String token = request.getHeader("Authorization");
            
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Unauthorized access attempt to {} - No token provided", path);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Token d'authentification manquant\"}");
                return;
            }
            
            // Vérifier le token auprès du service d'authentification
            if (!validateTokenWithAuthService(token)) {
                logger.warn("Invalid token for request to {}", path);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Token invalide ou expiré\"}");
                return;
            }
            
            logger.debug("Token validated successfully for request to {}", path);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAuthRequired(String path) {
        return !path.equals("/") 
                && !path.equals("/health")
                && !path.startsWith("/auth/register")
                && !path.startsWith("/auth/login");
    }
    
    /**
     * Valide le token auprès du microservice d'authentification
     * @param token Le token à valider
     * @return true si le token est valide, false sinon
     */
    private boolean validateTokenWithAuthService(String token) {
        try {
            // Appel au service d'authentification pour valider le token
            String validateUrl = authServiceUrl + "/auth/token";
            
            logger.debug("Validating token with Auth Service at: {}", validateUrl);
            
            // Créer le body avec le token
            String requestBody = "{\"token\":\"" + token + "\"}";
            
            // Créer les headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            // Appeler l'endpoint de validation (POST /auth/token)
            // Le service retourne un AuthResponseDTO avec le token si valide, ou une erreur 401 si invalide
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                validateUrl,
                org.springframework.http.HttpMethod.POST,
                entity,
                String.class
            );
            
            // Si le service répond 200, le token est valide
            boolean isValid = response.getStatusCode().is2xxSuccessful();
            logger.debug("Token validation result: {}", isValid);
            return isValid;
            
        } catch (HttpClientErrorException e) {
            // 401 ou 403 = token invalide
            logger.warn("Token validation failed with status: {}", e.getStatusCode());
            return false;
        } catch (Exception e) {
            // Erreur de connexion au service d'authentification
            logger.error("Error connecting to Auth Service: {}", e.getMessage());
            // Par sécurité, on refuse l'accès si le service Auth est inaccessible
            return false;
        }
    }
}
