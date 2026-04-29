package com.medcare.api.model;


/**
 * Data transfer object for Payroll in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDto {

    private Long id;
    private Long staffId;
    private String month;
    private Double baseSalary;
    private Double deductions;
    private Double netSalary;
    private LocalDate paidOn;
}
