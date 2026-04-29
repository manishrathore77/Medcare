package com.medcare.service.entity;


/**
 * JPA entity representing a diagnosis in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
public class Diagnosis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Appointment appointment;

    private String diagnosisName;
    private String severity;

    @Lob
    private String notes;
}
