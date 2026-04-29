package com.medcare.api.model;


/**
 * HTTP response payload for user resources.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    /** Application role name, e.g. PATIENT, IT_SUPPORT. */
    private String role;
    private Boolean active;
}
