package com.medcare.api.model;


/**
 * Data transfer object for Invoice in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private Long id;
    private Long appointmentId;
    private Double subtotal;
    private Double gst;
    private Double discount;
    private Double totalAmount;
    private InvoiceStatus status;
    private String insuranceClaimNo;
    private InsuranceStatus insuranceStatus;
}
