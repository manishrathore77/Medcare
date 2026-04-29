package com.medcare.service.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Successful login payload containing a JWT and the caller's coarse role and scopes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    /** Signed JWT (use in {@code Authorization: Bearer} header). */
    private String accessToken;
    /** Typically {@code Bearer}. */
    private String tokenType;
    /** Application role name (see {@link com.medcare.service.entity.User.Role}). */
    private String role;
    /** Fine-grained permission scope strings embedded in the token. */
    private List<String> scopes;
    /** Set when {@code role} is PATIENT: primary key of the linked {@code patients} row. */
    private Long patientId;
    /** Set when {@code role} is DOCTOR: primary key of the linked {@code doctors} row. */
    private Long doctorId;
}
