package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.InventoryLog} persistence.
 */

import com.medcare.service.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
}
