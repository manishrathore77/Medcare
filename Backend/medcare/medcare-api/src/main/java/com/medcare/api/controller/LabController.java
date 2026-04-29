package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.LabReportDto;
import com.medcare.api.model.LabTestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST contract for lab tests and reports.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/lab")
public interface LabController {

    @Operation(summary = "List lab tests", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_LAB_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/tests")
    ResponseEntity<?> listTests(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create lab test", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_LAB_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/tests", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createTest(@Valid @RequestBody LabTestDto dto);

    @Operation(summary = "List lab reports", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_LAB_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/reports")
    ResponseEntity<?> listReports(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(required = false) Long patientId,
                                   @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create lab report", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_LAB_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/reports", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createReport(@Valid @RequestBody LabReportDto dto);

    @Operation(summary = "Upload lab report file", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_LAB_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/reports/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadReport(@RequestParam @NotNull Long patientId,
                                   @RequestParam(required = false) Long labTestId,
                                   @RequestPart("file") MultipartFile file);

    @Operation(summary = "Download lab report file", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_LAB_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/reports/{reportId}/download")
    ResponseEntity<?> downloadReport(@PathVariable Long reportId);
}
