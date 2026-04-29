package com.medcare.api.model;


/**
 * Data transfer object for Treatment in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentDto {

    private Long id;
    private Long appointmentId;
    private String treatmentName;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private String instructions;
}
