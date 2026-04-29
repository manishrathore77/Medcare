package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.DiagnosisDto;
import com.medcare.api.model.PrescriptionDto;
import com.medcare.api.model.SoapNoteDto;
import com.medcare.api.model.TreatmentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for EMR artifacts (diagnoses, prescriptions, SOAP, treatments).
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/emr")
public interface EmrController {

    @Operation(summary = "List diagnoses", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/diagnoses")
    ResponseEntity<?> listDiagnoses(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create diagnosis", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/diagnoses", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createDiagnosis(@Valid @RequestBody DiagnosisDto dto);

    @Operation(summary = "List prescriptions", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/prescriptions")
    ResponseEntity<?> listPrescriptions(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create prescription", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/prescriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createPrescription(@Valid @RequestBody PrescriptionDto dto);

    @Operation(summary = "List SOAP notes", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/soap-notes")
    ResponseEntity<?> listSoapNotes(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create SOAP note", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/soap-notes", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createSoapNote(@Valid @RequestBody SoapNoteDto dto);

    @Operation(summary = "List treatments", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/treatments")
    ResponseEntity<?> listTreatments(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create treatment", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_EMR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/treatments", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createTreatment(@Valid @RequestBody TreatmentDto dto);
}
