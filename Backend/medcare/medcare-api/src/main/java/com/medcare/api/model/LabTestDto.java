package com.medcare.api.model;


/**
 * Data transfer object for LabTest in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabTestDto {

    private Long id;
    private Long appointmentId;
    private String testName;
    private String normalRange;
    private String resultValue;
    private LabTestStatus status;
}
