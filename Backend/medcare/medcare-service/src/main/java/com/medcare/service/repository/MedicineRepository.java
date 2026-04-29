package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Medicine} persistence.
 */

import com.medcare.service.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
