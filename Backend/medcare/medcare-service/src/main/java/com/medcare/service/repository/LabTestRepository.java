package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.LabTest} persistence.
 */

import com.medcare.service.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabTestRepository extends JpaRepository<LabTest, Long> {
}
