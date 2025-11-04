package com.alom.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenValidationRequestDTO {

    @NotBlank(message = "Token is required")
    private String token;
}