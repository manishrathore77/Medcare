package com.medcare.service.service.impl;

import com.medcare.service.entity.Treatment;
import com.medcare.service.repository.TreatmentRepository;
import com.medcare.service.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link TreatmentService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Treatment> getAll() {
        log.debug("Treatment listAll start");
        List<Treatment> all = treatmentRepository.findAll();
        log.debug("Treatment listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Treatment> getById(Long id) {
        log.debug("Treatment lookup id={}", id);
        return treatmentRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Treatment createTreatment(Treatment treatment) {
        Treatment saved = treatmentRepository.save(treatment);
        log.info("Treatment created id={} name={} appointmentId={}",
                saved.getId(),
                saved.getTreatmentName(),
                saved.getAppointment() != null ? saved.getAppointment().getId() : null);
        return saved;
    }
}
