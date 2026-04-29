package com.medcare.api.model;


/**
 * Data transfer object for Diagnosis in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisDto {

    private Long id;
    private Long appointmentId;
    private String diagnosisName;
    private String severity;
    private String notes;
}
