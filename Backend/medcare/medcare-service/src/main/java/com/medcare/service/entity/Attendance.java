package com.medcare.service.entity;


/**
 * JPA entity representing a attendance in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Staff staff;

    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
}
