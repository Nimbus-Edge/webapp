package com.cloud.webapp.service.serviceimpl;

import com.cloud.webapp.DTO.UserImageResponseDTO;
import com.cloud.webapp.DTO.UserRequestDTO;
import com.cloud.webapp.DTO.UserResponseDTO;
import com.cloud.webapp.entity.UserEntity;
import com.cloud.webapp.exceptions.Types.FieldAlreadyExistsException;
import com.cloud.webapp.exceptions.Types.GeneralServiceException;
import com.cloud.webapp.exceptions.Types.ResourceNotFoundException;
import com.cloud.webapp.exceptions.Types.UserNotVerifiedException;
import com.cloud.webapp.mapper.UserMapper;
import com.cloud.webapp.repository.UserRepository;
import com.cloud.webapp.service.UserService;
import com.cloud.webapp.service.aws.CloudWatchService;
import com.cloud.webapp.service.aws.S3Service;
import com.cloud.webapp.service.aws.SNSService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.cloud.webapp.utils.helpers.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final S3Service s3service;
    private SNSService snsService;
    private final CloudWatchService cloudWatchService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void checkIfUserVerified(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (!user.isVerified()) {
            throw new UserNotVerifiedException("User", "email", email);
        }
    }

    @Transactional
    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("createUser() Service hit started for userRequestDTO: {}", userRequestDTO);

            long dbStartTime = System.currentTimeMillis();

            if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
                throw new FieldAlreadyExistsException("Email", "email", userRequestDTO.getEmail());
            }

            String hashedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
            UserEntity user = UserEntity.builder()
                    .first_name(userRequestDTO.getFirst_name())
                    .last_name(userRequestDTO.getLast_name())
                    .email(userRequestDTO.getEmail())
                    .password(hashedPassword)
                    .verified(false)
                    .build();

            // Generate a unique verification token
            String verificationToken = generateVerificationToken();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(4);

            user.setVerificationToken(verificationToken);
            user.setTokenExpirationTime(expirationTime);

            userRepository.save(user);

            // Send SNS message
            String message = "{\"receiverEmail\":\"" + user.getEmail() + "\", \"token\":\"" + verificationToken + "\"}";
            snsService.publishToTopic(message);

            long dbEndTime = System.currentTimeMillis();
            cloudWatchService.recordDatabaseQueryTime("createUserDatabaseOperation", dbEndTime - dbStartTime);

            long endTime = System.currentTimeMillis();
            cloudWatchService.incrementApiCall("registerUser");
            cloudWatchService.recordApiResponseTime("registerUser", endTime - startTime);

            logger.info("createUser() Service hit completed for userRequestDTO: {}", userRequestDTO);

            return userMapper.toResponseDTOfromEntity(user);
        } catch (Exception e) {
            logger.error("Error in createUser() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            if (e instanceof FieldAlreadyExistsException) {
                throw (FieldAlreadyExistsException) e;
            }
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }


    @Override
    public UserResponseDTO getUserByEmail(String email) {
        try {
            checkIfUserVerified(email);
            long startTime = System.currentTimeMillis();
            logger.info("getUserByEmail() Service hit started for email: {}", email);

            // Record start time for the database query
            long dbStartTime = System.currentTimeMillis();

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Email", "email", email));

            long dbEndTime = System.currentTimeMillis();
            cloudWatchService.recordDatabaseQueryTime("getUserByEmailDatabaseOperation", dbEndTime - dbStartTime);

            logger.info("getUserByEmail() Service hit completed for email: {}", email);
            long endTime = System.currentTimeMillis();
            cloudWatchService.incrementApiCall("fetchUserDetails");
            cloudWatchService.recordApiResponseTime("fetchUserDetails", endTime - startTime);
            return userMapper.toResponseDTOfromEntity(user);
        } catch (Exception e) {
            logger.error("Error in getUserByEmail() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            if (e instanceof UserNotVerifiedException) {
                throw (UserNotVerifiedException) e;
            }
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserDetails(UserRequestDTO userRequestDTO, String authenticatedEmail) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("updateUserDetails() Service hit started for request: {}", userRequestDTO);

            if (userRequestDTO == null) {
                throw new IllegalArgumentException("Request body cannot be empty.");
            }
            checkIfUserVerified(userRequestDTO.getEmail());

            // Record start time for the database query
            long dbStartTime = System.currentTimeMillis();

            UserEntity userEntity = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedEmail));

            if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().equals(authenticatedEmail)) {
                throw new IllegalArgumentException("Email cannot be changed.");
            }

            BeanUtils.copyProperties(userRequestDTO, userEntity, getNullPropertyNamesHelper(userRequestDTO));
            if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
                userEntity.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            }

            UserEntity updatedUser = userRepository.save(userEntity);

            long dbEndTime = System.currentTimeMillis();
            cloudWatchService.recordDatabaseQueryTime("updateUserDetailsDatabaseOperation", dbEndTime - dbStartTime);

            long endTime = System.currentTimeMillis();
            cloudWatchService.incrementApiCall("updateUserDetails");
            cloudWatchService.recordApiResponseTime("updateUserDetails", endTime - startTime);
            logger.info("updateUserDetails() Service hit completed for request: {}", userRequestDTO);
            return userMapper.toResponseDTOfromEntity(updatedUser);
        } catch (Exception e) {
            logger.error("Error in updateUserDetails() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }

    @Override
    public UserImageResponseDTO uploadProfilePicture(String email, MultipartFile profilePic) {
        try {
            checkIfUserVerified(email);
            long startTime = System.currentTimeMillis();
            logger.info("uploadProfilePicture() Service hit started for request: {}", email);

            // Record start time for the database query
            long dbStartTime = System.currentTimeMillis();

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Email", "email", email));

            long dbEndTime = System.currentTimeMillis();
            cloudWatchService.recordDatabaseQueryTime("uploadProfilePictureDatabaseOperation", dbEndTime - dbStartTime);

            String s3ObjectKey = user.getId() + "/profilePic." + profilePic.getOriginalFilename().split("\\.")[1];
            String imageUrl = s3service.uploadImage(s3ObjectKey, profilePic);
            user.setS3ObjectKey(s3ObjectKey);
            user.setImageUrl(imageUrl);

            userRepository.save(user);
            long endTime = System.currentTimeMillis();
            cloudWatchService.incrementApiCall("uploadProfilePic");
            cloudWatchService.recordApiResponseTime("uploadProfilePic", endTime - startTime);
            logger.info("uploadProfilePicture() Service hit completed for request: {}", email);

            return UserImageResponseDTO.builder()
                    .url(user.getImageUrl())
                    .id(user.getId())
                    .fileName(profilePic.getOriginalFilename())
                    .uploadDate(LocalDateTime.now())
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            logger.error("Error in uploadProfilePicture() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            if (e instanceof FieldAlreadyExistsException) {
                throw (FieldAlreadyExistsException) e;
            }
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }

    @Override
    public UserImageResponseDTO getProfilePicture(String email) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("getProfilePicture() Service hit started for request: {}", email);

            // Record start time for the database query
            long dbStartTime = System.currentTimeMillis();

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            checkIfUserVerified(user.getEmail());
            long dbEndTime = System.currentTimeMillis();
            cloudWatchService.recordDatabaseQueryTime("getProfilePictureDatabaseOperation", dbEndTime - dbStartTime);

            if (user.getImageUrl() == null) {
                throw new ResourceNotFoundException("Profile Pic", "profilePic", email);
            }

            long endTime = System.currentTimeMillis();
            cloudWatchService.incrementApiCall("getProfilePic");
            cloudWatchService.recordApiResponseTime("getProfilePic", endTime - startTime);
            logger.info("getProfilePicture() Service hit completed for request: {}", email);

            return UserImageResponseDTO.builder()
                    .url(user.getImageUrl())
                    .id(user.getId())
                    .fileName(user.getS3ObjectKey())
                    .uploadDate(convertTimeToLocalDateTime(user.getAccount_updated()))
                    .userId(user.getId())
                    .build();
        } catch (ResourceNotFoundException e) {
            logger.error("Error in getProfilePicture() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            throw e;
        } catch (Exception e) {
            logger.error("Error in getProfilePicture() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }

    @Override
    public void deleteProfilePicture(String email) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("deleteProfilePicture() Service hit started for request: {}", email);

            // Record start time for the database query
            long dbStartTime = System.currentTimeMillis();

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Email", "email", email));
            checkIfUserVerified(user.getEmail());
            if (user.getS3ObjectKey() == null) {
                throw new ResourceNotFoundException("Profile Pic", "profilePic", email);
            }

            s3service.deleteImage(user.getS3ObjectKey());
            user.setS3ObjectKey(null);
            user.setImageUrl(null);

            userRepository.save(user);

            long dbEndTime = System.currentTimeMillis();
            cloudWatchService.recordDatabaseQueryTime("deleteProfilePictureDatabaseOperation", dbEndTime - dbStartTime);

            long endTime = System.currentTimeMillis();
            cloudWatchService.incrementApiCall("deleteProfilePic");
            cloudWatchService.recordApiResponseTime("deleteProfilePic", endTime - startTime);
            logger.info("deleteProfilePicture() Service hit completed for request: {}", email);
        } catch (ResourceNotFoundException e) {
            logger.error("Error in deleteProfilePicture() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            throw e;
        } catch (Exception e) {
            logger.error("Error in deleteProfilePicture() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }

    public boolean verifyUserByToken(String token) {
        try {
            logger.info("verifyUserByToken() Service hit started for token: {}", token);
            Optional<UserEntity> userOpt = userRepository.findByVerificationToken(token);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                if (user.isVerificationTokenExpired()) {
                    return false;
                }
                user.setVerified(true);
                user.setVerificationToken(null);
                userRepository.save(user);
                return true;
            }
            return false;
        }
        catch(Exception e){
            logger.error("Error in verifyUserByToken() service: {}", e.getMessage());
            logger.error("UserServiceImpl Service Layer Exception");
            throw new GeneralServiceException("UserServiceImpl Service Layer Exception");
        }
    }

}
