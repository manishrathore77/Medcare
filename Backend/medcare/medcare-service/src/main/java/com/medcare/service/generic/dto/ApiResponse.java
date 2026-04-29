package com.medcare.service.generic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Uniform JSON envelope for REST success and error payloads.
 *
 * @param <T> type of the {@link #getData() data} field
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Whether the operation succeeded from the client's perspective. */
    private boolean success;
    /** Human-readable summary or error text. */
    private String message;
    /** Optional response body; may be {@code null} on errors. */
    private T data;
    /** HTTP-style status code mirrored in the JSON body. */
    private int status;
}
