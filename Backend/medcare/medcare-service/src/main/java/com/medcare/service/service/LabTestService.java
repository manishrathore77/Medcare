package com.medcare.service.service;

import com.medcare.service.entity.LabTest;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for laboratory test orders and results.
 */
public interface LabTestService {

    /**
     * @return all lab tests
     */
    List<LabTest> getAll();

    /**
     * @param id lab test id
     * @return optional lab test when found
     */
    Optional<LabTest> getById(Long id);

    /**
     * @param labTest new lab test record
     * @return persisted lab test
     */
    LabTest createLabTest(LabTest labTest);
}
