package com.medcare.service.service.impl;

import com.medcare.service.entity.Medicine;
import com.medcare.service.repository.MedicineRepository;
import com.medcare.service.service.MedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.medcare.service.generic.exception.ResourceNotFoundException;

/**
 * Default {@link MedicineService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Medicine> getAll() {
        log.debug("Medicine listAll start");
        List<Medicine> all = medicineRepository.findAll();
        log.debug("Medicine listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Medicine> getById(Long id) {
        log.debug("Medicine lookup id={}", id);
        return medicineRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Medicine createMedicine(Medicine medicine) {
        Medicine saved = medicineRepository.save(medicine);
        log.info("Medicine saved medicineId={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    public Medicine updateMedicine(Long id, Medicine patch) {
        Medicine existing = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", id));
        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getBatchNo() != null) {
            existing.setBatchNo(patch.getBatchNo());
        }
        if (patch.getExpiryDate() != null) {
            existing.setExpiryDate(patch.getExpiryDate());
        }
        if (patch.getStockQuantity() != null) {
            existing.setStockQuantity(patch.getStockQuantity());
        }
        if (patch.getReorderLevel() != null) {
            existing.setReorderLevel(patch.getReorderLevel());
        }
        if (patch.getUnitPrice() != null) {
            existing.setUnitPrice(patch.getUnitPrice());
        }
        Medicine saved = medicineRepository.save(existing);
        log.info("Medicine updated medicineId={}", saved.getId());
        return saved;
    }

    @Override
    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine", id);
        }
        medicineRepository.deleteById(id);
        log.info("Medicine deleted medicineId={}", id);
    }
}
