package com.medcare.service.service.impl;

import com.medcare.service.entity.Patient;
import com.medcare.service.repository.PatientRepository;
import com.medcare.service.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link PatientService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Patient registerPatient(Patient patient) {
        Patient saved = patientRepository.save(patient);
        log.info("Patient registered patientId={}", saved.getId());
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> getById(Long id) {
        log.debug("Patient lookup by id={}", id);
        Optional<Patient> result = patientRepository.findById(id);
        if (result.isEmpty()) {
            log.debug("Patient not found id={}", id);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> getByUserId(Long userId) {
        log.debug("Patient lookup by userId={}", userId);
        return patientRepository.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        log.debug("Patient listAll start");
        List<Patient> all = patientRepository.findAll();
        log.debug("Patient listAll done count={}", all.size());
        return all;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findPatientsForDoctor(Long doctorId) {
        log.debug("Patient findForDoctor doctorId={}", doctorId);
        return patientRepository.findDistinctPatientsForDoctor(doctorId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Patient updatePatient(Long id, Patient patient) {
        log.info("Patient update start id={}", id);
        patient.setId(id);
        Patient saved = patientRepository.save(patient);
        log.info("Patient update done id={}", saved.getId());
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePatient(Long id) {
        log.info("Patient delete id={}", id);
        patientRepository.deleteById(id);
    }
}
