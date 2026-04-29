package com.medcare.service.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Public self-registration: creates a {@code PATIENT} account and linked patient profile.
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String gender;
    private LocalDate dob;
    private String address;
    private String emergencyContact;
}
