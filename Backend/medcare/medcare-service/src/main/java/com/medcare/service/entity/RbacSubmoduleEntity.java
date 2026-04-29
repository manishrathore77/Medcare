package com.medcare.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rbac_submodules")
@Getter
@Setter
public class RbacSubmoduleEntity {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private RbacModuleEntity module;

    private String name;

    private boolean active = true;
}
