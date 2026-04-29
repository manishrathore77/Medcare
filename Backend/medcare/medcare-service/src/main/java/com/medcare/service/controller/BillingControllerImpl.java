package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.BillingController}.
 */

import com.medcare.api.controller.BillingController;
import com.medcare.api.model.InvoiceDto;
import com.medcare.api.model.PaymentDto;
import com.medcare.service.entity.Appointment;
import com.medcare.service.entity.Invoice;
import com.medcare.service.entity.Payment;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.InvoiceService;
import com.medcare.service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BillingControllerImpl implements BillingController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    @Override
    @PreAuthorize("hasAuthority('BILLING_MANAGEMENT_INVOICES_READ')")
    public ResponseEntity<PagedResponse<InvoiceDto>> listInvoices(int page, int size) {
        List<Invoice> all = invoiceService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<InvoiceDto> content = all.subList(from, to).stream().map(this::toInvoiceDto).collect(Collectors.toList());
        PagedResponse<InvoiceDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('BILLING_MANAGEMENT_INVOICES_READ')")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoice(Long id) {
        return invoiceService.getById(id)
                .map(i -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toInvoiceDto(i), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('BILLING_MANAGEMENT_INVOICES_WRITE')")
    public ResponseEntity<ApiResponse<InvoiceDto>> createInvoice(InvoiceDto dto) {
        Invoice inv = new Invoice();
        if (dto.getAppointmentId() != null) {
            Appointment a = new Appointment();
            a.setId(dto.getAppointmentId());
            inv.setAppointment(a);
        }
        inv.setSubtotal(dto.getSubtotal());
        inv.setGst(dto.getGst());
        inv.setDiscount(dto.getDiscount());
        inv.setTotalAmount(dto.getTotalAmount());
        inv.setStatus(dto.getStatus() != null ? Invoice.Status.valueOf(dto.getStatus().name()) : Invoice.Status.PENDING);
        inv.setInsuranceClaimNo(dto.getInsuranceClaimNo());
        inv.setInsuranceStatus(dto.getInsuranceStatus() != null ? Invoice.InsuranceStatus.valueOf(dto.getInsuranceStatus().name()) : Invoice.InsuranceStatus.NOT_APPLIED);
        Invoice saved = invoiceService.createInvoice(inv);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toInvoiceDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_READ')")
    public ResponseEntity<PagedResponse<PaymentDto>> listPayments(int page, int size) {
        List<Payment> all = paymentService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<PaymentDto> content = all.subList(from, to).stream().map(this::toPaymentDto).collect(Collectors.toList());
        PagedResponse<PaymentDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('PROCUREMENT_MANAGEMENT_PAYMENT_MILESTONES_WRITE')")
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(PaymentDto dto) {
        Payment p = new Payment();
        if (dto.getInvoiceId() != null) {
            Invoice inv = new Invoice();
            inv.setId(dto.getInvoiceId());
            p.setInvoice(inv);
        }
        p.setAmount(dto.getAmount());
        p.setPaymentMode(dto.getPaymentMode() != null ? Payment.PaymentMode.valueOf(dto.getPaymentMode().name()) : null);
        p.setPaymentStatus(dto.getPaymentStatus() != null ? Payment.PaymentStatus.valueOf(dto.getPaymentStatus().name()) : null);
        p.setTransactionId(dto.getTransactionId());
        p.setGateway(dto.getGateway() != null ? Payment.Gateway.valueOf(dto.getGateway().name()) : null);
        p.setPaidAt(dto.getPaidAt());
        Payment saved = paymentService.createPayment(p);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toPaymentDto(saved), HttpStatus.CREATED.value()));
    }

    private InvoiceDto toInvoiceDto(Invoice i) {
        InvoiceDto d = new InvoiceDto();
        d.setId(i.getId());
        d.setAppointmentId(i.getAppointment() != null ? i.getAppointment().getId() : null);
        d.setSubtotal(i.getSubtotal());
        d.setGst(i.getGst());
        d.setDiscount(i.getDiscount());
        d.setTotalAmount(i.getTotalAmount());
        d.setStatus(i.getStatus() != null ? com.medcare.api.model.InvoiceStatus.valueOf(i.getStatus().name()) : null);
        d.setInsuranceClaimNo(i.getInsuranceClaimNo());
        d.setInsuranceStatus(i.getInsuranceStatus() != null ? com.medcare.api.model.InsuranceStatus.valueOf(i.getInsuranceStatus().name()) : null);
        return d;
    }

    private PaymentDto toPaymentDto(Payment p) {
        PaymentDto d = new PaymentDto();
        d.setId(p.getId());
        d.setInvoiceId(p.getInvoice() != null ? p.getInvoice().getId() : null);
        d.setAmount(p.getAmount());
        d.setPaymentMode(p.getPaymentMode() != null ? com.medcare.api.model.PaymentMode.valueOf(p.getPaymentMode().name()) : null);
        d.setPaymentStatus(p.getPaymentStatus() != null ? com.medcare.api.model.PaymentStatus.valueOf(p.getPaymentStatus().name()) : null);
        d.setTransactionId(p.getTransactionId());
        d.setGateway(p.getGateway() != null ? com.medcare.api.model.PaymentGateway.valueOf(p.getGateway().name()) : null);
        d.setPaidAt(p.getPaidAt());
        return d;
    }
}
