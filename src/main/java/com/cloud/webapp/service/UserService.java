package com.cloud.webapp.service;

import com.cloud.webapp.DTO.UserImageResponseDTO;
import com.cloud.webapp.DTO.UserRequestDTO;
import com.cloud.webapp.DTO.UserResponseDTO;
import com.cloud.webapp.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO updateUserDetails(UserRequestDTO userRequestDTO, String authenticatedEmail);
    UserImageResponseDTO uploadProfilePicture(String email, MultipartFile file);
    UserImageResponseDTO getProfilePicture(String email);
    void deleteProfilePicture(String email);
    boolean verifyUserByToken(String token);
}
