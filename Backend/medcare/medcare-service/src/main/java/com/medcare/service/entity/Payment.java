package com.medcare.service.entity;


/**
 * JPA entity representing a payment in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Invoice invoice;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private Gateway gateway;

    @Lob
    private String gatewayResponse;

    private LocalDateTime paidAt;

    public enum PaymentMode {
        CASH, CARD, UPI, INSURANCE
    }

    public enum PaymentStatus {
        PAID, FAILED, PENDING
    }

    public enum Gateway {
        RAZORPAY, STRIPE, PAYTM
    }
}
