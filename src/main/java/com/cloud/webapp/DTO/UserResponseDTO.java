package com.cloud.webapp.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    @Schema(description = "UUID of the user", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", readOnly = true)
    private String id;

    @Schema(description = "First name of the user", example = "Jane", required = true)
    private String first_name;

    @Schema(description = "Last name of the user", example = "Doe", required = true)
    private String last_name;

    @Schema(description = "Email of the user", example = "jane.doe@example.com", required = true)
    private String email;

    @Schema(description = "Date when the account was created", example = "2016-08-29T09:12:33.001Z", readOnly = true)
    private String account_created;

    @Schema(description = "Date when the account was last updated", example = "2016-08-29T09:12:33.001Z", readOnly = true)
    private String account_updated;

    private String s3ObjectKey;

    private String imageUrl;
}
