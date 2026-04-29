package com.medcare.api.model;


/**
 * Data transfer object for Payment in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long id;
    private Long invoiceId;
    private Double amount;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private PaymentGateway gateway;
    private LocalDateTime paidAt;
}
