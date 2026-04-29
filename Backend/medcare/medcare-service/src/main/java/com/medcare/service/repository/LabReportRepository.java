package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.LabReport} persistence.
 */

import com.medcare.service.entity.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabReportRepository extends JpaRepository<LabReport, Long> {
    List<LabReport> findByPatientId(Long patientId);
}
