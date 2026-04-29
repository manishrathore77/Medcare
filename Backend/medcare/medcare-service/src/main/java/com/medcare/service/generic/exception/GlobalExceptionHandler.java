package com.medcare.service.generic.exception;

import com.medcare.service.generic.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps common exceptions to {@link ApiResponse} JSON bodies for REST clients.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Converts bean validation failures to a field → message map.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                   WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        log.warn("Validation failed: {} field error(s)", errors.size());
        ApiResponse<Object> body = new ApiResponse<>(false, "Validation failed", errors,
                HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles explicit domain {@link ResourceNotFoundException}.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex,
                                                                   WebRequest request) {
        log.info("Resource not found: {}", ex.getMessage());
        ApiResponse<Object> body = new ApiResponse<>(false, ex.getMessage(), null,
                HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationConflictException.class)
    protected ResponseEntity<ApiResponse<Object>> handleRegistrationConflict(RegistrationConflictException ex,
                                                                             WebRequest request) {
        log.info("Registration conflict: {}", ex.getMessage());
        ApiResponse<Object> body = new ApiResponse<>(false, ex.getMessage(), null,
                HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    /**
     * Handles {@code @PreAuthorize} denials once the request reaches MVC dispatch.
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex,
                                                                       WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        String msg = "You do not have permission for this action. Sign in with a different role or contact an administrator.";
        ApiResponse<Object> body = new ApiResponse<>(false, msg, null,
                HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    /**
     * Fallback for unchecked errors that are not handled more specifically above.
     */
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex, WebRequest request) {
        log.error("Unhandled runtime exception", ex);
        ApiResponse<Object> body = new ApiResponse<>(false, ex.getMessage(), null,
                HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Last-resort handler for checked and unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex, WebRequest request) {
        log.error("Unexpected exception", ex);
        ApiResponse<Object> body = new ApiResponse<>(false, ex.getMessage(), null,
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
