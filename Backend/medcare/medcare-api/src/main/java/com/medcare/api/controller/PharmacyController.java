package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.InventoryLogDto;
import com.medcare.api.model.MedicineDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for medicines and inventory logs.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/pharmacy")
public interface PharmacyController {

    @Operation(summary = "List medicines", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/medicines")
    ResponseEntity<?> listMedicines(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create medicine", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/medicines", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createMedicine(@Valid @RequestBody MedicineDto dto);

    @Operation(summary = "Get medicine by id", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/medicines/{id}")
    ResponseEntity<?> getMedicine(@PathVariable("id") Long id);

    @Operation(summary = "Update medicine", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @PutMapping(value = "/medicines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updateMedicine(@PathVariable("id") Long id, @Valid @RequestBody MedicineDto dto);

    @Operation(summary = "Delete medicine", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @DeleteMapping("/medicines/{id}")
    ResponseEntity<?> deleteMedicine(@PathVariable("id") Long id);

    @Operation(summary = "List inventory logs", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/inventory")
    ResponseEntity<?> listInventory(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Create inventory log", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.MEDCARE_PHARMACY_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/inventory", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createInventoryLog(@Valid @RequestBody InventoryLogDto dto);
}
