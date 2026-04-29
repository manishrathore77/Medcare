package com.medcare.service.service.impl;

import com.medcare.service.entity.InventoryLog;
import com.medcare.service.repository.InventoryLogRepository;
import com.medcare.service.service.InventoryLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link InventoryLogService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryLogServiceImpl implements InventoryLogService {

    private final InventoryLogRepository inventoryLogRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<InventoryLog> getAll() {
        log.debug("InventoryLog listAll start");
        List<InventoryLog> all = inventoryLogRepository.findAll();
        log.debug("InventoryLog listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryLog> getById(Long id) {
        log.debug("InventoryLog lookup id={}", id);
        return inventoryLogRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InventoryLog createInventoryLog(InventoryLog inventoryLog) {
        InventoryLog saved = inventoryLogRepository.save(inventoryLog);
        log.info("InventoryLog created logId={} medicineId={} qty={}",
                saved.getId(),
                saved.getMedicine() != null ? saved.getMedicine().getId() : null,
                saved.getQuantity());
        return saved;
    }
}
