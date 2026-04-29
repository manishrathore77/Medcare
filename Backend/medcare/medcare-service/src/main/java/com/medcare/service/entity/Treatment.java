package com.medcare.service.entity;


/**
 * JPA entity representing a treatment in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "treatments")
@Getter
@Setter
public class Treatment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Appointment appointment;

    private String treatmentName;
    private LocalDate startDate;
    private LocalDate endDate;

    @Lob
    private String instructions;
}
