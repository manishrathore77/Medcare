package com.medcare.service.service;

import com.medcare.service.entity.InventoryLog;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for medicine stock movement history.
 */
public interface InventoryLogService {

    /**
     * @return all inventory log entries
     */
    List<InventoryLog> getAll();

    /**
     * @param id log id
     * @return optional log when found
     */
    Optional<InventoryLog> getById(Long id);

    /**
     * Records a stock adjustment or movement.
     *
     * @param inventoryLog populated log row
     * @return persisted log
     */
    InventoryLog createInventoryLog(InventoryLog inventoryLog);
}
