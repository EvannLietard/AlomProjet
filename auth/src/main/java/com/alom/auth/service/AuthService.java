package com.alom.auth.service;

import com.alom.auth.dto.AuthRequestDTO;
import com.alom.auth.dto.AuthResponseDTO;
import com.alom.auth.dto.TokenValidationRequestDTO;

public interface AuthService {

    AuthResponseDTO register(AuthRequestDTO request);
    AuthResponseDTO login(AuthRequestDTO request);
    Boolean isTokenValid(TokenValidationRequestDTO token);
}