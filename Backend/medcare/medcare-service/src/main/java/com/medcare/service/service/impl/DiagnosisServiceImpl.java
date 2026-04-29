package com.medcare.service.service.impl;

import com.medcare.service.entity.Diagnosis;
import com.medcare.service.repository.DiagnosisRepository;
import com.medcare.service.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link DiagnosisService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Diagnosis> getAll() {
        log.debug("Diagnosis listAll start");
        List<Diagnosis> all = diagnosisRepository.findAll();
        log.debug("Diagnosis listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Diagnosis> getById(Long id) {
        log.debug("Diagnosis lookup id={}", id);
        return diagnosisRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Diagnosis createDiagnosis(Diagnosis diagnosis) {
        Diagnosis saved = diagnosisRepository.save(diagnosis);
        log.info("Diagnosis created id={} name={} appointmentId={}",
                saved.getId(),
                saved.getDiagnosisName(),
                saved.getAppointment() != null ? saved.getAppointment().getId() : null);
        return saved;
    }
}
