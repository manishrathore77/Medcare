package com.medcare.service.service;

import com.medcare.service.entity.Treatment;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for treatment plans linked to encounters.
 */
public interface TreatmentService {

    /**
     * @return all treatments
     */
    List<Treatment> getAll();

    /**
     * @param id treatment id
     * @return optional treatment when found
     */
    Optional<Treatment> getById(Long id);

    /**
     * @param treatment new treatment row
     * @return persisted treatment
     */
    Treatment createTreatment(Treatment treatment);
}
