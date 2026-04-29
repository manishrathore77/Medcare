package com.medcare.api.controller;

import com.medcare.api.constants.APIConstants;
import com.medcare.api.model.InvoiceDto;
import com.medcare.api.model.PaymentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for invoices and payment milestones.
 * <p>Implemented by the {@code medcare-service} module.</p>
 */

@RequestMapping("/api/billing")
public interface BillingController {

    @Operation(summary = "List invoices", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.BILLING_MANAGEMENT_INVOICES_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/invoices")
    ResponseEntity<?> listInvoices(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get invoice by id", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.BILLING_MANAGEMENT_INVOICES_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/invoices/{id}")
    ResponseEntity<?> getInvoice(@PathVariable Long id);

    @Operation(summary = "Create invoice", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.BILLING_MANAGEMENT_INVOICES_WRITE})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/invoices", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceDto dto);

    @Operation(summary = "List payments", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {APIConstants.PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ})
    })
    @ApiResponse(responseCode = APIConstants.OK_CODE, description = APIConstants.OK_CODE_MSG)
    @GetMapping("/payments")
    ResponseEntity<?> listPayments(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Creates a new PaymentMilestone", security = {
            @SecurityRequirement(name = APIConstants.DEFAULT_SCHEME, scopes = {"PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_WRITE"})
    })
    @ApiResponse(responseCode = APIConstants.CREATED_CODE, description = APIConstants.CREATED_CODE_MSG)
    @PostMapping(value = "/payments", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDto dto);
}
