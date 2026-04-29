package com.medcare.api.model;


/**
 * HTTP response payload for clinic resources.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicResponse {

    private Long id;
    private String name;
    private String location;
    private String contactNumber;
}
