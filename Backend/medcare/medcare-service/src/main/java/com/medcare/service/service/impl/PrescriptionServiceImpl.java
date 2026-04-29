package com.medcare.service.service.impl;

import com.medcare.service.entity.Prescription;
import com.medcare.service.repository.PrescriptionRepository;
import com.medcare.service.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link PrescriptionService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getAll() {
        log.debug("Prescription listAll start");
        List<Prescription> all = prescriptionRepository.findAll();
        log.debug("Prescription listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Prescription> getById(Long id) {
        log.debug("Prescription lookup id={}", id);
        return prescriptionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> listForPatient(Long patientId) {
        log.debug("Prescription listForPatient patientId={}", patientId);
        return prescriptionRepository.findAllByAppointmentPatientId(patientId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Prescription createPrescription(Prescription prescription) {
        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Prescription created id={} medicine={} appointmentId={}",
                saved.getId(),
                saved.getMedicineName(),
                saved.getAppointment() != null ? saved.getAppointment().getId() : null);
        return saved;
    }
}
