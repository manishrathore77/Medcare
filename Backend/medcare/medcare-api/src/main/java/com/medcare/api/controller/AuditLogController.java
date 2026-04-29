package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for audit trail access and deletion.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/audit-logs")
public interface AuditLogController {

    @Operation(summary = "List audit logs", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_AUDIT_LOGS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping
    ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get audit log by id", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_AUDIT_LOGS_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/{id}")
    ResponseEntity<?> get(@PathVariable Long id);

    @Operation(summary = "Delete audit log", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_AUDIT_LOGS_DELETE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id);
}
