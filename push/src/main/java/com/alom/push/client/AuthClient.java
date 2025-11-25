package com.alom.push.client;

import com.alom.push.dto.TokenValidationRequestDTO;
import com.alom.push.dto.TokenValidationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8081}")
public interface AuthClient {

    @PostMapping("/auth/token")
    TokenValidationResponseDTO validateToken(@RequestBody TokenValidationRequestDTO request);
}