package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Invoice} persistence.
 */

import com.medcare.service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
