package com.medcare.service.service.impl;

import com.medcare.service.entity.Clinic;
import com.medcare.service.repository.ClinicRepository;
import com.medcare.service.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link ClinicService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Clinic createClinic(Clinic clinic) {
        Clinic saved = clinicRepository.save(clinic);
        log.info("Clinic created clinicId={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Clinic> getById(Long id) {
        log.debug("Clinic lookup id={}", id);
        return clinicRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Clinic> getAllClinics() {
        log.debug("Clinic listAll start");
        List<Clinic> all = clinicRepository.findAll();
        log.debug("Clinic listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Clinic updateClinic(Long id, Clinic clinic) {
        log.info("Clinic update id={}", id);
        clinic.setId(id);
        return clinicRepository.save(clinic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClinic(Long id) {
        log.info("Clinic delete id={}", id);
        clinicRepository.deleteById(id);
    }
}
