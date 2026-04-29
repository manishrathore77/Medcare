package com.medcare.api.model;


/**
 * Data transfer object for Prescription in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {

    private Long id;
    private Long appointmentId;
    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
}
