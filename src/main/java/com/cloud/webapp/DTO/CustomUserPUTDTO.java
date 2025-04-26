package com.cloud.webapp.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class CustomUserPUTDTO {

    @Schema(description = "First name of the user", example = "Jane")
    @JsonProperty("first_name")
    private String first_name;

    @Schema(description = "Last name of the user", example = "Doe")
    @JsonProperty("last_name")
    private String last_name;

    @Schema(description = "Email of the user", example = "jane.doe@example.com")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @JsonProperty("email")
    private String email;

    @Schema(description = "Password for the user", example = "s3cr3t", writeOnly = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
