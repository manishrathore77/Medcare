package com.medcare.api.model;


/**
 * Data transfer object for Staff in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {

    private Long id;
    private Long userId;
    private String name;
    private String department;
    private String role;
    private Double salary;
    private Boolean active;
}
