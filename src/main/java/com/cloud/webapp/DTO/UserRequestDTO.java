package com.cloud.webapp.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserRequestDTO {

    @Schema(description = "First name of the user", example = "Jane")
    @NotBlank(message = "First name cannot be blank")
    private String first_name;

    @Schema(description = "Last name of the user", example = "Doe")
    @NotBlank(message = "Last name cannot be blank")
    private String last_name;

    @Schema(description = "Email of the user", example = "jane.doe@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @Schema(description = "Password for the user", example = "s3cr3t", writeOnly = true)
    @NotBlank(message = "Password cannot be blank")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
