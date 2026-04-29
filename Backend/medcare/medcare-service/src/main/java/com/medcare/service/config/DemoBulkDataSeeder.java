package com.medcare.service.config;

import com.medcare.service.entity.Appointment;
import com.medcare.service.entity.Clinic;
import com.medcare.service.entity.Doctor;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.User;
import com.medcare.service.repository.AppointmentRepository;
import com.medcare.service.repository.ClinicRepository;
import com.medcare.service.repository.DoctorRepository;
import com.medcare.service.repository.PatientRepository;
import com.medcare.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Idempotent demo users and related rows so dashboards and lists have realistic volume.
 * Passwords are for local/dev only.
 */
@Slf4j
@Component
@Order(25)
@RequiredArgsConstructor
public class DemoBulkDataSeeder implements CommandLineRunner {

    private static final String DEMO_PW_PATIENT = "patient123";
    private static final String DEMO_PW_DOCTOR = "doctor123";
    private static final String DEMO_PW_RECEPTION = "reception123";
    private static final String DEMO_PW_STAFF = "staff123";
    private static final String DEMO_PW_IT = "itsupport123";

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        ensureSecondClinic();
        seedReceptionistsAndStaff();
        seedItSupportAccounts();
        seedPatientAccounts();
        seedExtraDoctors();
        seedDemoAppointments();
        log.info(
                "Demo dataset check complete (patients user01–10, doctors doctor02–05, receptionist2, staff01–05, itsupport2, sample appointments).");
    }

    private void ensureSecondClinic() {
        if (clinicRepository.count() >= 2) {
            return;
        }
        Clinic c = new Clinic();
        c.setName("Riverside Clinic");
        c.setLocation("250 River Rd");
        c.setContactNumber("+1-555-0199");
        clinicRepository.save(c);
        log.info("Seeded second demo clinic (Riverside)");
    }

    private void seedReceptionistsAndStaff() {
        ensureUser("receptionist2", DEMO_PW_RECEPTION, User.Role.RECEPTIONIST, "reception2@medcare.local", null);
        for (int i = 1; i <= 5; i++) {
            String u = String.format("staff%02d", i);
            ensureUser(u, DEMO_PW_STAFF, User.Role.RECEPTIONIST, u + "@medcare.local", "+1-555-100" + i);
        }
    }

    private void seedItSupportAccounts() {
        ensureUser("itsupport2", DEMO_PW_IT, User.Role.IT_SUPPORT, "itsupport2@medcare.local", null);
    }

    private void seedPatientAccounts() {
        record P(String user, String first, String last, String gender, LocalDate dob) {}
        List<P> rows = List.of(
                new P("patient01", "Alex", "Martinez", "M", LocalDate.of(1992, 3, 15)),
                new P("patient02", "Jordan", "Lee", "F", LocalDate.of(1988, 7, 22)),
                new P("patient03", "Sam", "Taylor", "X", LocalDate.of(1995, 11, 2)),
                new P("patient04", "Riley", "Nguyen", "F", LocalDate.of(2001, 1, 30)),
                new P("patient05", "Casey", "Brown", "M", LocalDate.of(1979, 9, 9)),
                new P("patient06", "Morgan", "Davis", "F", LocalDate.of(1990, 4, 18)),
                new P("patient07", "Quinn", "Wilson", "M", LocalDate.of(1983, 12, 5)),
                new P("patient08", "Avery", "Garcia", "F", LocalDate.of(1998, 6, 25)),
                new P("patient09", "Skyler", "Moore", "X", LocalDate.of(2003, 2, 14)),
                new P("patient10", "Jamie", "Clark", "M", LocalDate.of(1991, 8, 8)));
        for (P row : rows) {
            ensurePatient(row.user(), row.first(), row.last(), row.gender(), row.dob());
        }
    }

    private void seedExtraDoctors() {
        record D(String user, String license, String first, String last, String spec) {}
        List<D> rows = List.of(
                new D("doctor02", "MD-DEMO-002", "James", "Wilson", "Cardiology"),
                new D("doctor03", "MD-DEMO-003", "Emily", "Rodriguez", "Pediatrics"),
                new D("doctor04", "MD-DEMO-004", "Michael", "Patel", "Orthopedics"),
                new D("doctor05", "MD-DEMO-005", "Priya", "Kapoor", "Dermatology"));
        for (D row : rows) {
            ensureDoctor(row.user(), row.license(), row.first(), row.last(), row.spec());
        }
    }

    private void seedDemoAppointments() {
        if (appointmentRepository.count() > 0) {
            return;
        }
        List<Clinic> clinics = clinicRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();
        if (clinics.isEmpty() || patients.size() < 3 || doctors.size() < 2) {
            log.warn("Skipping demo appointments: need clinics, patients, and doctors");
            return;
        }
        Clinic c0 = clinics.getFirst();
        LocalDateTime base = LocalDateTime.now().withSecond(0).withNano(0);
        int n = Math.min(patients.size(), 8);
        for (int i = 0; i < n; i++) {
            Appointment a = new Appointment();
            a.setPatient(patients.get(i));
            a.setDoctor(doctors.get(i % doctors.size()));
            a.setClinic(c0);
            a.setAppointmentTime(base.plusDays(i % 3).plusHours((i % 5) + 9));
            a.setAppointmentType(Appointment.AppointmentType.IN_CLINIC);
            a.setStatus(i % 4 == 0 ? Appointment.Status.PENDING : Appointment.Status.CONFIRMED);
            appointmentRepository.save(a);
        }
        log.info("Seeded {} demo appointments", n);
    }

    private void ensureUser(String username, String rawPassword, User.Role role, String email, String phone) {
        userRepository
                .findByUsername(username)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(username);
                    u.setPassword(passwordEncoder.encode(rawPassword));
                    u.setRole(role);
                    u.setEmail(email);
                    u.setPhone(phone);
                    u.setIsActive(true);
                    User saved = userRepository.save(u);
                    log.info("Seeded user {} ({})", username, role);
                    return saved;
                });
    }

    private void ensurePatient(String username, String firstName, String lastName, String gender, LocalDate dob) {
        User u = userRepository
                .findByUsername(username)
                .orElseGet(() -> {
                    User nu = new User();
                    nu.setUsername(username);
                    nu.setPassword(passwordEncoder.encode(DEMO_PW_PATIENT));
                    nu.setRole(User.Role.PATIENT);
                    nu.setEmail(username + "@demo.medcare.local");
                    nu.setIsActive(true);
                    return userRepository.save(nu);
                });
        if (patientRepository.findByUserId(u.getId()).isPresent()) {
            return;
        }
        Patient p = new Patient();
        p.setUser(u);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setGender(gender);
        p.setDob(dob);
        p.setAddress("100 Demo St");
        p.setEmergencyContact("+1-555-0000");
        patientRepository.save(p);
        log.info("Seeded patient profile {}", username);
    }

    private void ensureDoctor(String username, String license, String firstName, String lastName, String specialty) {
        User u = userRepository
                .findByUsername(username)
                .orElseGet(() -> {
                    User nu = new User();
                    nu.setUsername(username);
                    nu.setPassword(passwordEncoder.encode(DEMO_PW_DOCTOR));
                    nu.setRole(User.Role.DOCTOR);
                    nu.setEmail(username + "@demo.medcare.local");
                    nu.setIsActive(true);
                    return userRepository.save(nu);
                });
        doctorRepository
                .findByLicenseNumber(license)
                .ifPresentOrElse(
                        d -> {
                            if (d.getUser() == null || !u.getId().equals(d.getUser().getId())) {
                                d.setUser(u);
                                doctorRepository.save(d);
                            }
                        },
                        () -> {
                            Doctor d = new Doctor();
                            d.setUser(u);
                            d.setFirstName(firstName);
                            d.setLastName(lastName);
                            d.setSpecialty(specialty);
                            d.setLicenseNumber(license);
                            d.setContactNumber("+1-555-0300");
                            d.setEmail(username + "@demo.medcare.local");
                            d.setIsActive(true);
                            doctorRepository.save(d);
                            log.info("Seeded doctor {} ({})", username, license);
                        });
    }
}
