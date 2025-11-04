package com.alom.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        
        logger.info("Incoming request: {} {} from {}", method, path, remoteAddr);
        
        filterChain.doFilter(request, response);
        
        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response.getStatus();
        
        logger.info("Response: {} for {} {} - Duration: {}ms", statusCode, method, path, duration);
    }
}
