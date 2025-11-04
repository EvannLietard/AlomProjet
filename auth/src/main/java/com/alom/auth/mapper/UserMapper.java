package com.alom.auth.mapper;

import com.alom.auth.dto.AuthResponseDTO;
import com.alom.auth.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "authToken", target = "token")
    AuthResponseDTO toAuthResponse(UserEntity user);
}