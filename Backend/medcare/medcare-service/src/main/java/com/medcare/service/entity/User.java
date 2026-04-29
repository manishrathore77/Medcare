package com.medcare.service.entity;


/**
 * JPA entity representing a user in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;
    private String phone;
    private Boolean isActive = true;

    public enum Role {
        PATIENT, DOCTOR, RECEPTIONIST, ADMIN, IT_SUPPORT
    }
}
