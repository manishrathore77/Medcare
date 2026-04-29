package com.medcare.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Top-level application module for the configuration hub (authorization documentation / RBAC UI).
 */
@Entity
@Table(name = "rbac_modules")
@Getter
@Setter
public class RbacModuleEntity {

    @Id
    @Column(length = 64)
    private String id;

    private String name;

    @Column(length = 1024)
    private String description;

    private boolean active = true;
}
