package com.medcare.service.generic.exception;

/**
 * Thrown when registration fails because username or email is already taken.
 */
public class RegistrationConflictException extends RuntimeException {

    public RegistrationConflictException(String message) {
        super(message);
    }
}
