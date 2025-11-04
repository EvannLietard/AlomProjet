package com.alom.auth.service;

import com.alom.auth.dto.AuthRequestDTO;
import com.alom.auth.dto.AuthResponseDTO;
import com.alom.auth.dto.TokenValidationRequestDTO;
import com.alom.auth.entity.UserEntity;
import com.alom.auth.mapper.UserMapper;
import com.alom.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponseDTO register(AuthRequestDTO request) {

        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new RuntimeException("Nickname already taken");
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        UserEntity user = new UserEntity(request.getNickname(), hashedPassword);
        user.setAuthToken(UUID.randomUUID().toString());

        userRepository.save(user);

        return userMapper.toAuthResponse(user);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {

        UserEntity user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        user.setAuthToken(UUID.randomUUID().toString());
        userRepository.save(user);

        return userMapper.toAuthResponse(user);
    }

    @Override
    public Boolean isTokenValid(TokenValidationRequestDTO token) {
        return userRepository.findByAuthToken(token.getToken()).isPresent();
    }
}