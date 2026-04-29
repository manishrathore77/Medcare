package com.medcare.api.model;


/**
 * HTTP request body for doctor operations.
 */

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String specialty;
    private String licenseNumber;
    private String contactNumber;
    private String email;
}
