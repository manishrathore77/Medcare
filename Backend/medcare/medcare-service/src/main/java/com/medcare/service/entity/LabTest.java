package com.medcare.service.entity;


/**
 * JPA entity representing a labtest in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lab_tests")
@Getter
@Setter
public class LabTest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Appointment appointment;

    private String testName;
    private String normalRange;
    private String resultValue;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ORDERED, COMPLETED
    }
}
