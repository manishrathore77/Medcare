package com.medcare.service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rbac_roles")
@Getter
@Setter
public class RbacRoleEntity {

    @Id
    @Column(length = 64)
    private String id;

    private String name;

    @Column(length = 2048)
    private String description;

    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rbac_role_module_ids", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "module_id", length = 64)
    private List<String> moduleIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rbac_role_permission_ids", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_id", length = 64)
    private List<String> permissionIds = new ArrayList<>();
}
