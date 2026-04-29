package com.medcare.api.model;


/**
 * HTTP request body for clinic operations.
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
public class ClinicRequest {

    @NotBlank
    private String name;
    private String location;
    private String contactNumber;
}
