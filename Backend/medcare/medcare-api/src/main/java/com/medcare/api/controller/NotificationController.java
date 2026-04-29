package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for user notifications.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/notifications")
public interface NotificationController {

    @Operation(summary = "List notifications", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_NOTIFICATIONS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping
    ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create notification", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_NOTIFICATIONS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> create(@Valid @RequestBody NotificationDto dto);
}
