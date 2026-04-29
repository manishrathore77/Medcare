package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Attendance} persistence.
 */

import com.medcare.service.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
