package com.medcare.service.service.impl;

import com.medcare.service.entity.Attendance;
import com.medcare.service.repository.AttendanceRepository;
import com.medcare.service.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link AttendanceService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAll() {
        log.debug("Attendance listAll start");
        List<Attendance> all = attendanceRepository.findAll();
        log.debug("Attendance listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> getById(Long id) {
        log.debug("Attendance lookup id={}", id);
        return attendanceRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attendance createAttendance(Attendance attendance) {
        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance created id={} staffId={} date={}",
                saved.getId(),
                saved.getStaff() != null ? saved.getStaff().getId() : null,
                saved.getDate());
        return saved;
    }
}
