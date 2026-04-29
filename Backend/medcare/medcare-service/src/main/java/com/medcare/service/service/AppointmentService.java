package com.medcare.service.service;

import com.medcare.service.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Application service contract for appointment booking, lookup, cancellation, and rescheduling.
 */
public interface AppointmentService {

    /**
     * Books a new appointment (pending then confirmed in the default implementation).
     *
     * @param appointment entity with doctor, patient, clinic, and time populated
     * @return persisted appointment
     */
    Appointment bookAppointment(Appointment appointment);

    /**
     * @param id appointment id
     * @return optional appointment when found
     */
    Optional<Appointment> getById(Long id);

    /**
     * Lists appointments for a clinic, or every appointment when {@code clinicId} is {@code null}.
     *
     * @param clinicId clinic filter, or {@code null} for all
     * @return matching appointments
     */
    List<Appointment> getByClinic(Long clinicId);

    /**
     * Lists appointments for a single patient.
     *
     * @param patientId patient primary key
     * @return appointments for that patient
     */
    List<Appointment> getByPatientId(Long patientId);

    /**
     * Lists appointments for a single doctor.
     *
     * @param doctorId doctor primary key
     * @return appointments for that doctor
     */
    List<Appointment> getByDoctorId(Long doctorId);

    /**
     * Marks an appointment as cancelled.
     *
     * @param appointmentId target id
     * @throws RuntimeException when the appointment does not exist
     */
    void cancelAppointment(Long appointmentId);

    /**
     * Moves an appointment to a new time if the slot is free for the same doctor.
     *
     * @param appointmentId target id
     * @param newTime       new start time
     * @return updated appointment
     * @throws RuntimeException when not found, doctor conflict, or invalid state
     */
    Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newTime);
}
