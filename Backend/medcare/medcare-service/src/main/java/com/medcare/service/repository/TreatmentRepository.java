package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Treatment} persistence.
 */

import com.medcare.service.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
}
