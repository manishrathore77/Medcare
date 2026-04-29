package com.medcare.service.service.impl;

import com.medcare.service.entity.Doctor;
import com.medcare.service.repository.DoctorRepository;
import com.medcare.service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link DoctorService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor createDoctor(Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor created doctorId={}", saved.getId());
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Doctor> getById(Long id) {
        log.debug("Doctor lookup id={}", id);
        return doctorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Doctor> getByUserId(Long userId) {
        log.debug("Doctor lookup by userId={}", userId);
        return doctorRepository.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Doctor> getActiveDoctors() {
        log.debug("Doctor listActive start");
        List<Doctor> list = doctorRepository.findByIsActiveTrue();
        log.debug("Doctor listActive count={}", list.size());
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor updateDoctor(Long id, Doctor doctor) {
        log.info("Doctor update id={}", id);
        doctor.setId(id);
        return doctorRepository.save(doctor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDoctor(Long id) {
        log.info("Doctor delete id={}", id);
        doctorRepository.deleteById(id);
    }
}
