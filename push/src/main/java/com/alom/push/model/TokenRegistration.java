package com.alom.push.model;

public class TokenRegistration {
    
    private String token;
    private String nickname;

    public TokenRegistration() {}
    
    public TokenRegistration(String token, String nickname) {
        this.token = token;
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}