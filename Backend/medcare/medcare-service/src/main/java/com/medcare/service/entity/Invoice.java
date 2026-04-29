package com.medcare.service.entity;


/**
 * JPA entity representing a invoice in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Appointment appointment;

    private Double subtotal;
    private Double gst;
    private Double discount;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String insuranceClaimNo;

    @Enumerated(EnumType.STRING)
    private InsuranceStatus insuranceStatus = InsuranceStatus.NOT_APPLIED;

    public enum Status {
        PENDING, PAID, CANCELLED
    }

    public enum InsuranceStatus {
        NOT_APPLIED, SUBMITTED, APPROVED, REJECTED
    }
}
