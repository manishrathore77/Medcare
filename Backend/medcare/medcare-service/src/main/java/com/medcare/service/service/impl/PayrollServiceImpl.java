package com.medcare.service.service.impl;

import com.medcare.service.entity.Payroll;
import com.medcare.service.repository.PayrollRepository;
import com.medcare.service.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link PayrollService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payroll> getAll() {
        log.debug("Payroll listAll start");
        List<Payroll> all = payrollRepository.findAll();
        log.debug("Payroll listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Payroll> getById(Long id) {
        log.debug("Payroll lookup id={}", id);
        return payrollRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payroll createPayroll(Payroll payroll) {
        Payroll saved = payrollRepository.save(payroll);
        log.info("Payroll created id={} staffId={} month={}",
                saved.getId(),
                saved.getStaff() != null ? saved.getStaff().getId() : null,
                saved.getMonth());
        return saved;
    }
}
