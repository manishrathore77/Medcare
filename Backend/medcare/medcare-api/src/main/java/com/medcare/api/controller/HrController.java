package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.AttendanceDto;
import com.medcare.api.model.PayrollDto;
import com.medcare.api.model.StaffDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for staff, attendance, and payroll.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/hr")
public interface HrController {

    @Operation(summary = "List staff", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_HR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/staff")
    ResponseEntity<?> listStaff(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create staff", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_HR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createStaff(@Valid @RequestBody StaffDto dto);

    @Operation(summary = "List attendance", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_HR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/attendance")
    ResponseEntity<?> listAttendance(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create attendance", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_HR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/attendance", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createAttendance(@Valid @RequestBody AttendanceDto dto);

    @Operation(summary = "List payroll", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_HR_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/payroll")
    ResponseEntity<?> listPayroll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create payroll", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_HR_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/payroll", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createPayroll(@Valid @RequestBody PayrollDto dto);
}
