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

import java.io.IOException;

@Component
@Order(2)
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

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
            
            //Flo ajoute içi la partie qui fait l'appel au microservice d'authentification pour vérifier le token
            logger.debug("Token received for request to {}: {}", path, token);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAuthRequired(String path) {
        return !path.equals("/") 
                && !path.equals("/health")
                && !path.startsWith("/api/auth/register")
                && !path.startsWith("/api/auth/login");
    }
}
