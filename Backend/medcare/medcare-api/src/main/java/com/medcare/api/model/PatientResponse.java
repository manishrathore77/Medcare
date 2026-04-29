package com.medcare.api.model;


/**
 * HTTP response payload for patient resources.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private String address;
    private String emergencyContact;
    private String insuranceProvider;
    private String insuranceNumber;
}
