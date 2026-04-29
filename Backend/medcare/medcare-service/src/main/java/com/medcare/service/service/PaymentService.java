package com.medcare.service.service;

import com.medcare.service.entity.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for payments against invoices.
 */
public interface PaymentService {

    /**
     * @return all payments
     */
    List<Payment> getAll();

    /**
     * @param id payment id
     * @return optional payment when found
     */
    Optional<Payment> getById(Long id);

    /**
     * @param payment new payment row
     * @return persisted payment
     */
    Payment createPayment(Payment payment);
}
