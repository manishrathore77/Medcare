package com.medcare.service.service;

import com.medcare.service.entity.Payroll;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for payroll runs.
 */
public interface PayrollService {

    /**
     * @return all payroll records
     */
    List<Payroll> getAll();

    /**
     * @param id payroll id
     * @return optional payroll when found
     */
    Optional<Payroll> getById(Long id);

    /**
     * @param payroll new payroll entry
     * @return persisted payroll
     */
    Payroll createPayroll(Payroll payroll);
}
