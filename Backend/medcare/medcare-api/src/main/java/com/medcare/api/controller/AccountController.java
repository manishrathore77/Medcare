package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Authenticated user's own account (any role with a valid JWT).
 */
@RequestMapping("/api/account")
public interface AccountController {

    @Operation(summary = "Current user profile", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME)
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/me")
    ResponseEntity<?> me();
}
