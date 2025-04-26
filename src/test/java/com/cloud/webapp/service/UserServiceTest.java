package com.cloud.webapp.service;

import static org.junit.jupiter.api.Assertions.*;


import com.cloud.webapp.DTO.UserRequestDTO;

import com.cloud.webapp.exceptions.Types.GeneralServiceException;
import com.cloud.webapp.mapper.UserMapper;
import com.cloud.webapp.repository.UserRepository;
import com.cloud.webapp.service.aws.CloudWatchService;
import com.cloud.webapp.service.serviceimpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @BeforeAll
    static void setup() {
        System.setProperty("DB_URL", "jdbc:mysql://localhost:3306/users");
        System.setProperty("DB_USERNAME", "root");
        System.setProperty("DB_PASSWORD", "password");
        System.setProperty("SENDGRID_API_KEY", "");
        System.setProperty("SENDGRID_FROM_EMAIL", "");
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CloudWatchService cloudWatchService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetUserByEmail_UserNotFound() {
        String email = "nonexistent@example.com";
        assertThrows(GeneralServiceException.class, () -> {
            userService.getUserByEmail(email);
        });
    }


    @Test
    void testUpdateUserDetails_UserNotFound() {
        UserRequestDTO request = new UserRequestDTO("Alice", "Smith", "alice@example.com", "newpassword");
        String authenticatedEmail = "nonexistent@example.com";

        assertThrows(GeneralServiceException.class, () -> {
            userService.updateUserDetails(request, authenticatedEmail);
        });
    }

    @Test
    void testUpdateUserDetails_EmailCannotChange() {
        UserRequestDTO request = new UserRequestDTO("Alice", "Smith", "newemail@example.com", "newpassword");
        String authenticatedEmail = "alice@example.com";

        assertThrows(GeneralServiceException.class, () -> {
            userService.updateUserDetails(request, authenticatedEmail);
        });
    }
}
