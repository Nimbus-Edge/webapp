package com.cloud.webapp.mapper.mapperimpl;

import com.cloud.webapp.DTO.CustomUserPUTDTO;
import com.cloud.webapp.DTO.UserRequestDTO;
import com.cloud.webapp.DTO.UserResponseDTO;
import com.cloud.webapp.entity.UserEntity;
import com.cloud.webapp.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapperImpl implements UserMapper {
    private ModelMapper modelMapper;

    @Override
    public UserEntity toEntityFromRequestDTO(UserRequestDTO userModel) {
        return modelMapper.map(userModel, UserEntity.class);
    }

    @Override
    public UserResponseDTO toResponseDTOfromEntity(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserResponseDTO.class);
    }


    @Override
    public UserResponseDTO toResponseDTOFromRequestDTO(UserRequestDTO userModel) {
        return modelMapper.map(userModel, UserResponseDTO.class);
    }

    @Override
    public UserRequestDTO toRequestDTOFromCustomUserRequestDTO(CustomUserPUTDTO customUserPUTDTO) {
        return modelMapper.map(customUserPUTDTO, UserRequestDTO.class);
    }

}
