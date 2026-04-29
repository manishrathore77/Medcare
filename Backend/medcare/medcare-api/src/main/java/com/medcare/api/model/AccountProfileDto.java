package com.medcare.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Current signed-in user profile (no password).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileDto {

    private Long id;
    private String username;
    private String email;
    /** Application role name, e.g. RECEPTIONIST, DOCTOR. */
    private String role;
    private String phone;
    private Boolean active;
}
