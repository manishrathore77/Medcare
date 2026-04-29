package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Doctor} persistence.
 */

import com.medcare.service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserId(Long userId);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    List<Doctor> findBySpecialty(String specialty);

    List<Doctor> findByIsActiveTrue();
}
