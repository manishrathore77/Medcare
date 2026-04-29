package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Staff} persistence.
 */

import com.medcare.service.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
