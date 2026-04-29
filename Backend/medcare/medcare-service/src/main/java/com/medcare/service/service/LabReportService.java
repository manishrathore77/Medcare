package com.medcare.service.service;

import com.medcare.service.entity.LabReport;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for finalized lab report documents.
 */
public interface LabReportService {

    /**
     * @return all lab reports
     */
    List<LabReport> getAll();

    /**
     * @param patientId patient id
     * @return reports linked to patient
     */
    List<LabReport> getAllByPatientId(Long patientId);

    /**
     * @param id report id
     * @return optional report when found
     */
    Optional<LabReport> getById(Long id);

    /**
     * @param labReport new report metadata
     * @return persisted report
     */
    LabReport createLabReport(LabReport labReport);
}
