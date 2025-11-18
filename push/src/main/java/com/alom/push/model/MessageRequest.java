package com.alom.push.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequest {

    @NotNull(message = "Le nickname ne peut pas être null")
    @NotBlank(message = "Le nickname est requis")
    private String nickname;

    @NotNull(message = "Le message ne peut pas être null")
    @NotBlank(message = "Le message est requis")
    private String message;

    public MessageRequest() {}

    public MessageRequest(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}