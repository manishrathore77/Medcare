package com.medcare.api.model;


/**
 * Data transfer object for SoapNote in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoapNoteDto {

    private Long id;
    private Long appointmentId;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
}
