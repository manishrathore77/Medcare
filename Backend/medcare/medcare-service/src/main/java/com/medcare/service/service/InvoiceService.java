package com.medcare.service.service;

import com.medcare.service.entity.Invoice;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for billing invoices.
 */
public interface InvoiceService {

    /**
     * @return all invoices
     */
    List<Invoice> getAll();

    /**
     * @param id invoice id
     * @return optional invoice when found
     */
    Optional<Invoice> getById(Long id);

    /**
     * @param invoice new invoice aggregate
     * @return persisted invoice
     */
    Invoice createInvoice(Invoice invoice);
}
