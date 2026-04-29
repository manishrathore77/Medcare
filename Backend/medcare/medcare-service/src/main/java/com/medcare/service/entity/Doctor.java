package com.medcare.service.entity;


/**
 * JPA entity representing a doctor in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String firstName;
    private String lastName;
    private String specialty;

    @Column(unique = true)
    private String licenseNumber;

    private String contactNumber;
    private String email;
    private Double consultationFee;
    private Double totalEarnings = 0.0;
    private Boolean isActive = true;
}
