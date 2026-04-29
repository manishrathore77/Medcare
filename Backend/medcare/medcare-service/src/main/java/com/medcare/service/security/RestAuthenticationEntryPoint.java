package com.medcare.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medcare.service.generic.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns JSON 401 responses when no valid authentication is present for a protected resource.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.debug("Unauthorized {} {}: {}", request.getMethod(), request.getRequestURI(),
                authException.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Void> body = new ApiResponse<>(false,
                "Please sign in again. Your session may have expired or your sign-in token is missing.",
                null,
                HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
