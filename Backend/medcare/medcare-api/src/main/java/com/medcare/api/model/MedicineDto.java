package com.medcare.api.model;


/**
 * Data transfer object for Medicine in API contracts.
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
public class MedicineDto {

    private Long id;
    private String name;
    private String batchNo;
    private LocalDate expiryDate;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Double unitPrice;
}
