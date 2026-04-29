package com.medcare.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rbac_permissions")
@Getter
@Setter
public class RbacPermissionEntity {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "submodule_id")
    private RbacSubmoduleEntity submodule;

    private String permKey;

    private String label;

    /** JWT / API scope string (e.g. MEDCARE_PHARMACY_READ). */
    private String scope;

    /** CREATE, READ, UPDATE, DELETE, CUSTOM */
    private String kind;

    private boolean active = true;
}
