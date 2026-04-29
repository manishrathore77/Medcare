package com.medcare.api.model;


/**
 * HTTP response payload for appointment resources.
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
public class AppointmentResponse {

    private Long id;
    private Long doctorId;
    private Long patientId;
    private Long clinicId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private AppointmentType appointmentType;
}
