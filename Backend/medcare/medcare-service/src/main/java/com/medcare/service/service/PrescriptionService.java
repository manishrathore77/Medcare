package com.medcare.service.service;

import com.medcare.service.entity.Prescription;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for prescriptions issued during visits.
 */
public interface PrescriptionService {

    /**
     * @return all prescriptions
     */
    List<Prescription> getAll();

    /**
     * Prescriptions issued under visits for the given patient.
     *
     * @param patientId patient id
     * @return prescriptions linked via appointment
     */
    List<Prescription> listForPatient(Long patientId);

    /**
     * @param id prescription id
     * @return optional prescription when found
     */
    Optional<Prescription> getById(Long id);

    /**
     * @param prescription new prescription
     * @return persisted prescription
     */
    Prescription createPrescription(Prescription prescription);
}
