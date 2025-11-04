package com.alom.auth.mapper;

import com.alom.auth.dto.AuthResponseDTO;
import com.alom.auth.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    AuthResponseDTO toAuthResponse(UserEntity user);
}