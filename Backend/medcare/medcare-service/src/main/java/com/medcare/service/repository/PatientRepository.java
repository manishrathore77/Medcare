package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Patient} persistence.
 */

import com.medcare.service.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    @Query("SELECT DISTINCT p FROM Appointment a JOIN a.patient p WHERE a.doctor.id = :doctorId")
    List<Patient> findDistinctPatientsForDoctor(@Param("doctorId") Long doctorId);
}
