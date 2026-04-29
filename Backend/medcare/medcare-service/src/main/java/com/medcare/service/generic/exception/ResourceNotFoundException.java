package com.medcare.service.generic.exception;

/**
 * Raised when a domain object cannot be located by identifier.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message free-text explanation
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * @param resource logical resource name (e.g. {@code "Patient"})
     * @param id       missing primary key
     */
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}
