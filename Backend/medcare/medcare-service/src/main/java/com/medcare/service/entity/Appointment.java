package com.medcare.service.entity;


/**
 * JPA entity representing a appointment in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private Clinic clinic;

    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum AppointmentType {
        IN_CLINIC, VIDEO
    }

    public enum Status {
        PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
    }
}
