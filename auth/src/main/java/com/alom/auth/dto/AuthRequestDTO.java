package com.alom.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank(message = "Nickname is required")
    private String nickname;

    @NotBlank(message = "Password is required")
    private String password;
}