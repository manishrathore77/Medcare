package com.medcare.service.service.impl;

import com.medcare.service.entity.LabTest;
import com.medcare.service.repository.LabTestRepository;
import com.medcare.service.service.LabTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link LabTestService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<LabTest> getAll() {
        log.debug("LabTest listAll start");
        List<LabTest> all = labTestRepository.findAll();
        log.debug("LabTest listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LabTest> getById(Long id) {
        log.debug("LabTest lookup id={}", id);
        return labTestRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LabTest createLabTest(LabTest labTest) {
        LabTest saved = labTestRepository.save(labTest);
        log.info("LabTest created id={} name={} appointmentId={}",
                saved.getId(),
                saved.getTestName(),
                saved.getAppointment() != null ? saved.getAppointment().getId() : null);
        return saved;
    }
}
