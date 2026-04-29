package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.PatientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for patient demographics and insurance metadata.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/patients")
public interface PatientController {

    @Operation(summary = "List patients", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.PATIENT_MANAGEMENT_PATIENTS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping
    ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "Get patient by id", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.PATIENT_MANAGEMENT_PATIENTS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable Long id);

    @Operation(summary = "Create patient", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.PATIENT_MANAGEMENT_PATIENTS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> create(@Valid @RequestBody PatientRequest request);

    @Operation(summary = "Update patient", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.PATIENT_MANAGEMENT_PATIENTS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PatientRequest request);

    @Operation(summary = "Delete patient", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.PATIENT_MANAGEMENT_PATIENTS_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id);
}
