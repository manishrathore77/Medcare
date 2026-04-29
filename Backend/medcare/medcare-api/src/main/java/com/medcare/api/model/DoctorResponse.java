package com.medcare.api.model;


/**
 * HTTP response payload for doctor resources.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;
    private String licenseNumber;
    private String contactNumber;
    private String email;
    private Boolean active;
}
