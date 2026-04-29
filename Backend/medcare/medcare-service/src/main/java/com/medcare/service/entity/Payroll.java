package com.medcare.service.entity;


/**
 * JPA entity representing a payroll in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payroll")
@Getter
@Setter
public class Payroll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Staff staff;

    private String month;
    private Double baseSalary;
    private Double deductions;
    private Double netSalary;

    private LocalDate paidOn;
}
