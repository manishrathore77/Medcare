package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.AuditLog} persistence.
 */

import com.medcare.service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
