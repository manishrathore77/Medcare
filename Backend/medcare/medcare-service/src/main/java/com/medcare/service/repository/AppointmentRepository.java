package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Appointment} persistence.
 */

import com.medcare.service.entity.Appointment;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByClinicId(Long clinicId);

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.status <> 'CANCELLED' AND a.appointmentTime = :appointmentTime")
    List<Appointment> findConflictingAppointments(Long doctorId, LocalDateTime appointmentTime);
}
