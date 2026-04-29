package com.medcare.service.service;

import com.medcare.service.entity.Medicine;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for pharmacy medicine master data.
 */
public interface MedicineService {

    /**
     * @return all medicine rows
     */
    List<Medicine> getAll();

    /**
     * @param id medicine id
     * @return optional medicine when found
     */
    Optional<Medicine> getById(Long id);

    /**
     * @param medicine new or updated medicine (without id for create)
     * @return persisted medicine
     */
    Medicine createMedicine(Medicine medicine);

    /**
     * Updates an existing medicine row.
     *
     * @param id      primary key
     * @param patch   field values (null fields may be ignored by implementation)
     * @return updated entity
     */
    Medicine updateMedicine(Long id, Medicine patch);

    /**
     * Deletes a medicine by id.
     */
    void deleteMedicine(Long id);
}
