package com.medcare.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medcare.service.generic.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns JSON 403 responses when an authenticated user lacks required authorities.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.debug("Forbidden {} {}: {}", request.getMethod(), request.getRequestURI(),
                accessDeniedException.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String msg = "You do not have permission for this action. Use an account with the right role, or ask an administrator to grant access.";
        ApiResponse<Void> body = new ApiResponse<>(false, msg, null,
                HttpStatus.FORBIDDEN.value());
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
