package com.medcare.service.service.impl;

import com.medcare.service.entity.Invoice;
import com.medcare.service.repository.InvoiceRepository;
import com.medcare.service.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link InvoiceService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAll() {
        log.debug("Invoice listAll start");
        List<Invoice> all = invoiceRepository.findAll();
        log.debug("Invoice listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getById(Long id) {
        log.debug("Invoice lookup id={}", id);
        return invoiceRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Invoice createInvoice(Invoice invoice) {
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created id={} appointmentId={} total={}",
                saved.getId(),
                saved.getAppointment() != null ? saved.getAppointment().getId() : null,
                saved.getTotalAmount());
        return saved;
    }
}
