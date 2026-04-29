package com.medcare.service.service.impl;

import com.medcare.service.entity.Appointment;
import com.medcare.service.repository.AppointmentRepository;
import com.medcare.service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Default {@link AppointmentService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    /**
     * {@inheritDoc}
     * 
     * @param appointment appointment to book
     * @return booked appointment
     * @throws RuntimeException when the appointment is not found or the new time is not available
     */
    @Override
    public Appointment bookAppointment(Appointment appointment) {
        log.info("Appointment book start doctorId={} patientId={} clinicId={}",
                appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getClinic() != null ? appointment.getClinic().getId() : null);
        appointment.setStatus(Appointment.Status.PENDING);
        Appointment saved = appointmentRepository.save(appointment);
        saved.setStatus(Appointment.Status.CONFIRMED);
        Appointment confirmed = appointmentRepository.save(saved);
        log.info("Appointment book done appointmentId={} time={}", confirmed.getId(), confirmed.getAppointmentTime());
        return confirmed;
    }

    /**
     * {@inheritDoc}
     * 
     * @param id appointment id
     * @return optional appointment
     * @throws RuntimeException when the appointment is not found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> getById(Long id) {
        log.debug("Appointment lookup id={}", id);
        return appointmentRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getByClinic(Long clinicId) {
        if (clinicId == null) {
            log.debug("Appointment list all clinics");
            List<Appointment> all = appointmentRepository.findAll();
            log.debug("Appointment list all count={}", all.size());
            return all;
        }
        log.debug("Appointment list by clinicId={}", clinicId);
        List<Appointment> list = appointmentRepository.findByClinicId(clinicId);
        log.debug("Appointment list by clinic count={}", list.size());
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getByPatientId(Long patientId) {
        log.debug("Appointment list by patientId={}", patientId);
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getByDoctorId(Long doctorId) {
        log.debug("Appointment list by doctorId={}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelAppointment(Long appointmentId) {
        log.info("Appointment cancel start id={}", appointmentId);
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment cancel failed not found id={}", appointmentId);
                    return new RuntimeException("Appointment not found");
                });
        appt.setStatus(Appointment.Status.CANCELLED);
        appointmentRepository.save(appt);
        log.info("Appointment cancel done id={}", appointmentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newTime) {
        log.info("Appointment reschedule start id={} newTime={}", appointmentId, newTime);
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment reschedule failed not found id={}", appointmentId);
                    return new RuntimeException("Appointment not found");
                });
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                appt.getDoctor().getId(), newTime);
        if (!conflicts.isEmpty()) {
            log.warn("Appointment reschedule conflict appointmentId={} doctorId={} newTime={}",
                    appointmentId, appt.getDoctor().getId(), newTime);
            throw new RuntimeException("Selected slot already booked");
        }
        appt.setAppointmentTime(newTime);
        appt.setStatus(Appointment.Status.CONFIRMED);
        Appointment saved = appointmentRepository.save(appt);
        log.info("Appointment reschedule done id={} newTime={}", saved.getId(), saved.getAppointmentTime());
        return saved;
    }
}
