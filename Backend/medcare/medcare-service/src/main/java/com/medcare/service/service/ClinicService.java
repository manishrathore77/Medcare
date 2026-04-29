package com.medcare.service.service;

import com.medcare.service.entity.Clinic;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for clinic master data.
 */
public interface ClinicService {

    /**
     * @param clinic new clinic entity
     * @return persisted clinic
     */
    Clinic createClinic(Clinic clinic);

    /**
     * @param id clinic id
     * @return optional clinic when found
     */
    Optional<Clinic> getById(Long id);

    /**
     * @return all clinics
     */
    List<Clinic> getAllClinics();

    /**
     * @param id    clinic id
     * @param clinic replacement data
     * @return updated clinic
     */
    Clinic updateClinic(Long id, Clinic clinic);

    /**
     * @param id clinic id
     */
    void deleteClinic(Long id);
}
