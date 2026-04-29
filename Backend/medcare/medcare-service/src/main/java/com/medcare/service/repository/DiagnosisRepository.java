package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Diagnosis} persistence.
 */

import com.medcare.service.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
