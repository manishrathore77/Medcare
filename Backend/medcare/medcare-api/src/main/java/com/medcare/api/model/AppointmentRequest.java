package com.medcare.api.model;


/**
 * HTTP request body for appointment operations.
 */

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @NotNull
    private Long doctorId;
    @NotNull
    private Long patientId;
    @NotNull
    private Long clinicId;
    @NotNull
    private LocalDateTime appointmentTime;
    @NotNull
    private AppointmentType appointmentType;
}
