package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.DoctorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for doctor profiles.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/doctors")
public interface DoctorController {

    @Operation(summary = "List doctors", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_DOCTORS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping
    ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get doctor by id", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_DOCTORS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable Long id);

    @Operation(summary = "Create doctor", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_DOCTORS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> create(@Valid @RequestBody DoctorRequest request);

    @Operation(summary = "Update doctor", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_DOCTORS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody DoctorRequest request);

    @Operation(summary = "Delete doctor", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_DOCTORS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id);
}
