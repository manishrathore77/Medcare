package com.medcare.service.service;

import com.medcare.service.entity.Staff;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for HR staff records.
 */
public interface StaffService {

    /**
     * @return all staff rows
     */
    List<Staff> getAll();

    /**
     * @param id staff id
     * @return optional staff when found
     */
    Optional<Staff> getById(Long id);

    /**
     * @param staff new staff member
     * @return persisted staff
     */
    Staff createStaff(Staff staff);
}
