package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Prescription} persistence.
 */

import com.medcare.service.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.id = :patientId")
    List<Prescription> findAllByAppointmentPatientId(@Param("patientId") Long patientId);
}
