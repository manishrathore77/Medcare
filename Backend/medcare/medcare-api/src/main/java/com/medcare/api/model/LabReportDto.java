package com.medcare.api.model;


/**
 * Data transfer object for LabReport in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabReportDto {

    private Long id;
    private Long labTestId;
    private Long patientId;
    private String reportFileUrl;
    private String fileName;
    private String contentType;
    private String uploadedAt;
}
