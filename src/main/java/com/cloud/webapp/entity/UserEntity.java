package com.cloud.webapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.cloud.webapp.utils.constants.USER_TABLE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = USER_TABLE)
public class UserEntity extends AuditableEntity {

    @Id
    @Column(name = "id", columnDefinition = "char(36)", updatable = false, nullable = false)
    private String id;

    @PrePersist
    public void generateUUID() {
        this.id = UUID.randomUUID().toString();
    }

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name="s3object_key")
    private String s3ObjectKey;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name="verified")
    private boolean verified;

    @Column(name="verification_token")
    private String verificationToken;

    @Column(name="token_expiration_time")
    private LocalDateTime tokenExpirationTime;

    public boolean isVerificationTokenExpired() {
        return tokenExpirationTime.isBefore(LocalDateTime.now());
    }

}
