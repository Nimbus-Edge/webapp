package com.cloud.webapp.controller;

import com.cloud.webapp.DTO.CustomUserPUTDTO;
import com.cloud.webapp.DTO.UserImageResponseDTO;
import com.cloud.webapp.DTO.UserRequestDTO;
import com.cloud.webapp.DTO.UserResponseDTO;
import com.cloud.webapp.mapper.UserMapper;
import com.cloud.webapp.service.UserService;
import com.cloud.webapp.service.aws.CloudWatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import static com.cloud.webapp.utils.helpers.*;

@Validated
@Tag(name = "User Details",
        description = "Endpoints to perform CRUD Ops" +
                " on User - authenticated and authorized")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserMapper userMapper;

    @Operation(summary = "User Registration",
            description = "Create a user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success - User has been created"),
            @ApiResponse(responseCode = "405", description = "Bad Request - Method Not Allowed"),
            @ApiResponse(responseCode = "400", description =
                    "Bad Request - Verify Request Body, Path variables,Query params are not allowed"),
    })
    @PostMapping()
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("POST /v1/user hit started with request body {}", userRequestDTO);
        UserResponseDTO response = userService.createUser(userRequestDTO);
        logger.info("POST /v1/user hit completed with request body {}", userRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch User Profile",
            description = "For a registered user, fetch the user's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - User details fetched"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access restricted"),
            @ApiResponse(responseCode = "405", description = "Bad Request - Method Not Allowed, " +
                    "only GET/PUT are supported"),
    })
    @GetMapping("/self")
    public ResponseEntity<UserResponseDTO> fetchUserDetails() {
        logger.info("GET /v1/user/self hit started");
        String authenticatedEmail = getAuthenticatedUserEmailHelper();
        UserResponseDTO userResponse = userService.getUserByEmail(authenticatedEmail);
        logger.info("GET /v1/user/self hit completed with response: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Update User Profile",
            description = "For a registered user, update the user's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success - User details updated"),
            @ApiResponse(responseCode = "400", description =
                    "Bad Request - Verify Request Body, Path variables,Query params are not allowed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access restricted"),
            @ApiResponse(responseCode = "405", description = "Bad Request - Method Not Allowed, " +
                    "only GET/PUT are supported"),
    })
    @PutMapping("/self")
    public ResponseEntity<UserResponseDTO> updateUserDetails(@Valid @RequestBody CustomUserPUTDTO customUserPUTDTO) {
        logger.info("PUT /v1/user/self hit started with request body: {}", customUserPUTDTO);
        if (isRequestBodyEmptyHelper(customUserPUTDTO)) {
            logger.info("PUT /v1/user/self hit completed");
            return ResponseEntity.badRequest().build();
        }
        String authenticatedEmail = getAuthenticatedUserEmailHelper();
        UserRequestDTO userRequestDTO = userMapper.toRequestDTOFromCustomUserRequestDTO(customUserPUTDTO);
        UserResponseDTO updatedUser = userService.updateUserDetails(userRequestDTO, authenticatedEmail);
        logger.info("PUT /v1/user/self hit completed");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/self/pic")
    public ResponseEntity<UserImageResponseDTO> uploadProfilePic(
            @RequestHeader(value = HttpHeaders.CONTENT_LENGTH, required = false) String contentLength,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            @RequestParam("profilePic") MultipartFile profilePic,
            WebRequest webRequest) {
        logger.info("POST /v1/user/self/pic hit started");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        responseHeaders.setPragma("no-cache");
        if (!webRequest.getParameterMap().isEmpty()) {
            logger.error("Query parameters are not allowed for {} endpoint", "/self/pic");
            logger.info("POST /self/pic hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }
        String authenticatedEmail = getAuthenticatedUserEmailHelper();
        UserImageResponseDTO response = userService.uploadProfilePicture(authenticatedEmail, profilePic);
        logger.info("POST /v1/user/self/pic hit completed with response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/self/pic")
    public ResponseEntity<UserImageResponseDTO> getProfilePic(
            @RequestHeader(value = HttpHeaders.CONTENT_LENGTH, required = false) String contentLength,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            WebRequest webRequest) {
        logger.info("GET /v1/user/self/pic hit started");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        responseHeaders.setPragma("no-cache");
        if (!webRequest.getParameterMap().isEmpty()) {
            logger.error("Query parameters are not allowed for {} endpoint", "/self/pic");
            logger.info("GET /self/pic hit completed with error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }
        if (contentLength != null && Integer.parseInt(contentLength) > 0) {
            logger.error("Payload is not allowed for {} endpoint", "/self/pic");
            logger.info("GET /self/pic hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }

        if (contentType != null && !contentType.equals(MediaType.ALL_VALUE)) {
            logger.error("Content type is not allowed for {} endpoint", "/self/pic");
            logger.info("GET /self/pic hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }
        String authenticatedEmail = getAuthenticatedUserEmailHelper();
        UserImageResponseDTO response = userService.getProfilePicture(authenticatedEmail);
        logger.info("GET /v1/user/self/pic hit completed with response: {}", response);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(response);
    }

    @DeleteMapping("/self/pic")
    public ResponseEntity<Void> deleteProfilePic(
            @RequestHeader(value = HttpHeaders.CONTENT_LENGTH, required = false) String contentLength,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            WebRequest webRequest) {
        logger.info("DELETE /v1/user/self/pic hit started");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        responseHeaders.setPragma("no-cache");

        if (!webRequest.getParameterMap().isEmpty()) {
            logger.error("Query parameters are not allowed for {} endpoint", "/self/pic");
            logger.info("DELETE /self/pic hit completed with error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }

        if (contentLength != null && Integer.parseInt(contentLength) > 0) {
            logger.error("Payload is not allowed for {} endpoint", "/self/pic");
            logger.info("DELETE /self/pic hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }

        if (contentType != null && !contentType.equals(MediaType.ALL_VALUE)) {
            logger.error("Content type is not allowed for {} endpoint", "/self/pic");
            logger.info("DELETE /self/pic hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }
        String authenticatedEmail = getAuthenticatedUserEmailHelper();
        userService.deleteProfilePicture(authenticatedEmail);
        logger.info("DELETE /v1/user/self/pic hit completed successfully");
        return ResponseEntity.noContent()
                .headers(responseHeaders)
                .build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        logger.info("GET /v1/user/verify hit started for token: {}", token);
        boolean isVerified = userService.verifyUserByToken(token);
        logger.info("GET /v1/user/verify hit completed for token {} with response: {}", token, isVerified);
        if (isVerified) {
            return ResponseEntity.ok("User verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Verification link expired or invalid");
        }
    }
}
