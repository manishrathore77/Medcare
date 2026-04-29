package com.medcare.service.service;

import com.medcare.service.entity.Diagnosis;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for clinical diagnoses linked to encounters.
 */
public interface DiagnosisService {

    /**
     * @return all diagnoses
     */
    List<Diagnosis> getAll();

    /**
     * @param id diagnosis id
     * @return optional diagnosis when found
     */
    Optional<Diagnosis> getById(Long id);

    /**
     * @param diagnosis new diagnosis row
     * @return persisted diagnosis
     */
    Diagnosis createDiagnosis(Diagnosis diagnosis);
}
