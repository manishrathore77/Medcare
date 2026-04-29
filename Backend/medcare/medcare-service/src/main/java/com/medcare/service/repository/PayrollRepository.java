package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Payroll} persistence.
 */

import com.medcare.service.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
}
