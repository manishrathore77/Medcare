package com.medcare.service.service;

import com.medcare.service.entity.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for patient registration and profile maintenance.
 */
public interface PatientService {

    /**
     * Persists a new patient aggregate.
     *
     * @param patient populated entity (typically without {@code id})
     * @return saved entity including generated {@code id}
     */
    Patient registerPatient(Patient patient);

    /**
     * Loads a patient by primary key.
     *
     * @param id patient id
     * @return optional patient when found
     */
    Optional<Patient> getById(Long id);

    /**
     * Resolves the patient profile linked to a user account.
     *
     * @param userId owning user id
     * @return optional patient when a link exists
     */
    Optional<Patient> getByUserId(Long userId);

    /**
     * @return all patients (unpaged; callers should paginate at controller layer)
     */
    List<Patient> getAllPatients();

    /**
     * Patients who have at least one appointment with the given doctor.
     *
     * @param doctorId doctor id
     * @return distinct patient entities
     */
    List<Patient> findPatientsForDoctor(Long doctorId);

    /**
     * Overwrites mutable fields for an existing patient.
     *
     * @param id      patient id to update
     * @param patient replacement field values
     * @return persisted entity
     */
    Patient updatePatient(Long id, Patient patient);

    /**
     * Deletes a patient by id.
     *
     * @param id patient id
     */
    void deletePatient(Long id);
}
