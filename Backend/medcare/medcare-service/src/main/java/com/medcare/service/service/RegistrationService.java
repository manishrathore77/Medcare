package com.medcare.service.service;

import com.medcare.service.auth.AuthResponse;
import com.medcare.service.auth.RegisterRequest;

/**
 * Handles public sign-up for patient accounts.
 */
public interface RegistrationService {

    /**
     * Creates a {@code PATIENT} user, linked {@link com.medcare.service.entity.Patient} row, and returns JWT payload.
     *
     * @param request validated registration body
     * @return token bundle for immediate use
     * @throws com.medcare.service.generic.exception.RegistrationConflictException on duplicate username or email
     */
    AuthResponse registerPatient(RegisterRequest request);
}
