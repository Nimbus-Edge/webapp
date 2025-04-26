package com.cloud.webapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.cloud.webapp.utils.helpers.getCurrentTimeUtil;


@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@Data
public class AuditableEntity {
    @Column(name = "account_created", nullable = false)
    public String account_created = getCurrentTimeUtil();

    @Column(name = "account_updated", nullable = false)
    public String account_updated = getCurrentTimeUtil();

    @PreUpdate
    public void preUpdate() {
        this.account_updated = getCurrentTimeUtil();
    }
}