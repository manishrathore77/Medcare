package com.medcare.service.service;

import com.medcare.service.entity.Doctor;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for doctor profiles.
 */
public interface DoctorService {

    /**
     * @param doctor new doctor entity
     * @return persisted doctor
     */
    Doctor createDoctor(Doctor doctor);

    /**
     * @param id doctor id
     * @return optional doctor when found
     */
    Optional<Doctor> getById(Long id);

    /**
     * Resolves the doctor profile linked to a user account.
     *
     * @param userId owning user id
     * @return optional doctor when a link exists
     */
    Optional<Doctor> getByUserId(Long userId);

    /**
     * @return doctors flagged active in persistence
     */
    List<Doctor> getActiveDoctors();

    /**
     * @param id    doctor id
     * @param doctor replacement data
     * @return updated doctor
     */
    Doctor updateDoctor(Long id, Doctor doctor);

    /**
     * @param id doctor id
     */
    void deleteDoctor(Long id);
}
