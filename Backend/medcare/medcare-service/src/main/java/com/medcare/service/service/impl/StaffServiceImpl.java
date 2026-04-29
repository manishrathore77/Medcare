package com.medcare.service.service.impl;

import com.medcare.service.entity.Staff;
import com.medcare.service.repository.StaffRepository;
import com.medcare.service.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link StaffService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAll() {
        log.debug("Staff listAll start");
        List<Staff> all = staffRepository.findAll();
        log.debug("Staff listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getById(Long id) {
        log.debug("Staff lookup id={}", id);
        return staffRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Staff createStaff(Staff staff) {
        Staff saved = staffRepository.save(staff);
        log.info("Staff created staffId={} name={}", saved.getId(), saved.getName());
        return saved;
    }
}
