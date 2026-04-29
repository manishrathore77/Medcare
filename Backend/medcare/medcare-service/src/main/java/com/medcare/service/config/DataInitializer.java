package com.medcare.service.config;

import com.medcare.service.entity.Clinic;
import com.medcare.service.entity.Doctor;
import com.medcare.service.entity.User;
import com.medcare.service.repository.ClinicRepository;
import com.medcare.service.repository.DoctorRepository;
import com.medcare.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds development data when the application starts (e.g. default admin account).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setEmail("admin@medcare.local");
            admin.setIsActive(true);
            User saved = userRepository.save(admin);
            log.info("Seeded default admin user (username=admin)");
            return saved;
        });

        if (clinicRepository.count() == 0) {
            Clinic clinic = new Clinic();
            clinic.setName("Medcare Main Clinic");
            clinic.setLocation("100 Health Way");
            clinic.setContactNumber("+1-555-0100");
            clinicRepository.save(clinic);
            log.info("Seeded demo clinic");
        }

        ensureDoctorLogin();
        ensureReceptionistLogin();
        ensureItSupportLogin();
    }

    /**
     * Demo IT support account: {@code itsupport} / {@code itsupport123}.
     */
    private void ensureItSupportLogin() {
        userRepository.findByUsername("itsupport").orElseGet(() -> {
            User u = new User();
            u.setUsername("itsupport");
            u.setPassword(passwordEncoder.encode("itsupport123"));
            u.setRole(User.Role.IT_SUPPORT);
            u.setEmail("itsupport@medcare.local");
            u.setIsActive(true);
            User saved = userRepository.save(u);
            log.info("Seeded IT support user (username=itsupport, password=itsupport123)");
            return saved;
        });
    }

    /**
     * Demo front-desk account: {@code receptionist} / {@code reception123}.
     */
    private void ensureReceptionistLogin() {
        userRepository.findByUsername("receptionist").orElseGet(() -> {
            User u = new User();
            u.setUsername("receptionist");
            u.setPassword(passwordEncoder.encode("reception123"));
            u.setRole(User.Role.RECEPTIONIST);
            u.setEmail("reception@medcare.local");
            u.setIsActive(true);
            User saved = userRepository.save(u);
            log.info("Seeded receptionist user (username=receptionist, password=reception123)");
            return saved;
        });
    }

    /**
     * Idempotent: ensures demo clinician exists, user {@code doctor} / {@code doctor123} exists, and {@code doctors.user_id}
     * points at that user. Without the link, JWT login works but {@code getByUserId} fails and appointment APIs throw.
     */
    private void ensureDoctorLogin() {
        Doctor demoDoctor = doctorRepository.findByLicenseNumber("MD-DEMO-001").orElseGet(() -> {
            Doctor doctor = new Doctor();
            doctor.setFirstName("Sarah");
            doctor.setLastName("Chen");
            doctor.setSpecialty("General Practice");
            doctor.setLicenseNumber("MD-DEMO-001");
            doctor.setContactNumber("+1-555-0200");
            doctor.setEmail("sarah.chen@medcare.local");
            doctor.setIsActive(true);
            Doctor saved = doctorRepository.save(doctor);
            log.info("Seeded demo doctor row (license MD-DEMO-001)");
            return saved;
        });

        User docUser = userRepository.findByUsername("doctor").orElseGet(() -> {
            User u = new User();
            u.setUsername("doctor");
            u.setPassword(passwordEncoder.encode("doctor123"));
            u.setRole(User.Role.DOCTOR);
            u.setEmail("doctor@medcare.local");
            u.setIsActive(true);
            User saved = userRepository.save(u);
            log.info("Seeded doctor user (username=doctor, password=doctor123)");
            return saved;
        });

        boolean relink = demoDoctor.getUser() == null
                || demoDoctor.getUser().getId() == null
                || !docUser.getId().equals(demoDoctor.getUser().getId());
        if (relink) {
            demoDoctor.setUser(docUser);
            doctorRepository.save(demoDoctor);
            log.info("Linked doctor user id={} to doctor profile id={}", docUser.getId(), demoDoctor.getId());
        }
    }
}
