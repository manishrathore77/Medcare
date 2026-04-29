package com.medcare.service.service.impl;

import com.medcare.service.entity.Payment;
import com.medcare.service.repository.PaymentRepository;
import com.medcare.service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link PaymentService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAll() {
        log.debug("Payment listAll start");
        List<Payment> all = paymentRepository.findAll();
        log.debug("Payment listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> getById(Long id) {
        log.debug("Payment lookup id={}", id);
        return paymentRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPayment(Payment payment) {
        Payment saved = paymentRepository.save(payment);
        log.info("Payment created id={} invoiceId={} amount={} status={}",
                saved.getId(),
                saved.getInvoice() != null ? saved.getInvoice().getId() : null,
                saved.getAmount(),
                saved.getPaymentStatus());
        return saved;
    }
}
