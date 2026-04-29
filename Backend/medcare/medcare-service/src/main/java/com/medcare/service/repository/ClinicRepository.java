package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Clinic} persistence.
 */

import com.medcare.service.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}
