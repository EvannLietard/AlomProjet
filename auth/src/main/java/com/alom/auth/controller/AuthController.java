package com.alom.auth.controller;

import com.alom.auth.dto.AuthRequestDTO;
import com.alom.auth.dto.AuthResponseDTO;
import com.alom.auth.dto.TokenValidationRequestDTO;
import com.alom.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody AuthRequestDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/token")
    public Boolean validateToken(@Valid @RequestBody TokenValidationRequestDTO request) {
        return authService.isTokenValid(request);
    }

    @PostMapping("/user")
    public AuthResponseDTO getUserByToken(@Valid @RequestBody TokenValidationRequestDTO request) {
        return authService.getUserByToken(request);
    }
}