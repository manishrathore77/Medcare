package com.medcare.api.model;


/**
 * HTTP request body for patient operations.
 */

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String gender;
    private LocalDate dob;
    private String address;
    private String emergencyContact;
    private String insuranceProvider;
    private String insuranceNumber;
}
