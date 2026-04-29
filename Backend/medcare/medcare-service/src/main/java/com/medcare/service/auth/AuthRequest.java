package com.medcare.service.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Credentials submitted to {@code POST /api/auth/login}.
 */
@Getter
@Setter
public class AuthRequest {

    /** Unique login name. */
    @NotBlank
    private String username;

    /** Plain-text password (transport should use HTTPS in production). */
    @NotBlank
    private String password;
}
