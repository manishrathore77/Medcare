package com.medcare.service.service.impl;

import com.medcare.service.entity.LabReport;
import com.medcare.service.repository.LabReportRepository;
import com.medcare.service.service.LabReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link LabReportService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LabReportServiceImpl implements LabReportService {

    private final LabReportRepository labReportRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<LabReport> getAll() {
        log.debug("LabReport listAll start");
        List<LabReport> all = labReportRepository.findAll();
        log.debug("LabReport listAll count={}", all.size());
        return all;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabReport> getAllByPatientId(Long patientId) {
        log.debug("LabReport listByPatient start patientId={}", patientId);
        List<LabReport> all = labReportRepository.findByPatientId(patientId);
        log.debug("LabReport listByPatient count={} patientId={}", all.size(), patientId);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LabReport> getById(Long id) {
        log.debug("LabReport lookup id={}", id);
        return labReportRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LabReport createLabReport(LabReport labReport) {
        LabReport saved = labReportRepository.save(labReport);
        log.info("LabReport created id={} labTestId={}",
                saved.getId(),
                saved.getLabTest() != null ? saved.getLabTest().getId() : null);
        return saved;
    }
}
