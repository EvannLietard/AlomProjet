package com.alom.auth.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {

    private String nickname;
    private String password;
}