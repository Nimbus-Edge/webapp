package com.cloud.webapp.mapper;

import com.cloud.webapp.DTO.CustomUserPUTDTO;
import com.cloud.webapp.DTO.UserRequestDTO;
import com.cloud.webapp.DTO.UserResponseDTO;
import com.cloud.webapp.entity.UserEntity;

public interface UserMapper {
    UserEntity toEntityFromRequestDTO(UserRequestDTO userModel);
    UserResponseDTO toResponseDTOfromEntity(UserEntity userEntity);
    UserResponseDTO toResponseDTOFromRequestDTO(UserRequestDTO userModel);
    UserRequestDTO toRequestDTOFromCustomUserRequestDTO(CustomUserPUTDTO customUserPUTDTO);
}