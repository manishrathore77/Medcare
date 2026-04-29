package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Payment} persistence.
 */

import com.medcare.service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
