package com.alom.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    private String nickname;
    private String passwordHash;
    private String authToken;

    public UserEntity(String nickname, String passwordHash) {
        this.nickname = nickname;
        this.passwordHash = passwordHash;
    }
}