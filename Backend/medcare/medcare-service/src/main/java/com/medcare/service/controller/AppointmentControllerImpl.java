package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.AppointmentController}.
 */

import com.medcare.api.controller.AppointmentController;
import com.medcare.api.model.AppointmentRequest;
import com.medcare.api.model.AppointmentResponse;
import com.medcare.api.model.AppointmentStatus;
import com.medcare.api.model.AppointmentType;
import com.medcare.service.entity.Appointment;
import com.medcare.service.entity.Clinic;
import com.medcare.service.entity.Doctor;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.service.AppointmentService;
import com.medcare.service.service.DoctorService;
import com.medcare.service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AppointmentControllerImpl implements AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_APPOINTMENTS_READ')")
    public ResponseEntity<PagedResponse<AppointmentResponse>> list(int page, int size) {
        User user = currentUser();
        List<Appointment> all = resolveListForUser(user);
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<AppointmentResponse> content = all.subList(from, to).stream().map(this::toResponse).collect(Collectors.toList());
        int totalPages = size <= 0 ? 0 : (int) ((all.size() + (long) size - 1) / size);
        PagedResponse<AppointmentResponse> resp = new PagedResponse<>(content, all.size(), page, size,
                totalPages, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_APPOINTMENTS_READ')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(Long id) {
        User user = currentUser();
        return appointmentService.getById(id)
                .filter(a -> mayAccessAppointment(user, a))
                .map(a -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toResponse(a), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_APPOINTMENTS_WRITE')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(AppointmentRequest req) {
        User user = currentUser();
        if (user.getRole() == User.Role.PATIENT) {
            Long myPatientId = patientService.getByUserId(user.getId()).map(Patient::getId).orElseThrow();
            req.setPatientId(myPatientId);
        }
        if (user.getRole() == User.Role.DOCTOR) {
            Long myDoctorId = doctorService.getByUserId(user.getId()).map(Doctor::getId)
                    .orElseThrow(() -> new IllegalStateException("Doctor login is not linked to a doctor profile; restart the API to run data seed."));
            req.setDoctorId(myDoctorId);
        }
        Appointment a = new Appointment();
        Doctor d = new Doctor();
        d.setId(req.getDoctorId());
        a.setDoctor(d);
        Patient p = new Patient();
        p.setId(req.getPatientId());
        a.setPatient(p);
        Clinic c = new Clinic();
        c.setId(req.getClinicId());
        a.setClinic(c);
        a.setAppointmentTime(req.getAppointmentTime());
        a.setAppointmentType(toEntityType(req.getAppointmentType()));
        a.setStatus(Appointment.Status.PENDING);
        Appointment saved = appointmentService.bookAppointment(a);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toResponse(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_APPOINTMENTS_WRITE')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> update(Long id, AppointmentRequest req) {
        User user = currentUser();
        Appointment existing = appointmentService.getById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value()));
        }
        if (!mayAccessAppointment(user, existing)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value()));
        }
        Appointment updated = appointmentService.rescheduleAppointment(id, req.getAppointmentTime());
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toResponse(updated), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_APPOINTMENTS_WRITE')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        User user = currentUser();
        Appointment existing = appointmentService.getById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value()));
        }
        if (!mayAccessAppointment(user, existing)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value()));
        }
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    private User currentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow();
    }

    private List<Appointment> resolveListForUser(User user) {
        if (user.getRole() == User.Role.PATIENT) {
            Long patientId = patientService.getByUserId(user.getId()).map(Patient::getId).orElseThrow();
            return appointmentService.getByPatientId(patientId);
        }
        if (user.getRole() == User.Role.DOCTOR) {
            Optional<Doctor> od = doctorService.getByUserId(user.getId());
            if (od.isEmpty()) {
                return Collections.emptyList();
            }
            return appointmentService.getByDoctorId(od.get().getId());
        }
        return appointmentService.getByClinic(null);
    }

    /**
     * Staff (non-patient, non-doctor) see any appointment; patients and doctors only their own rows.
     */
    private boolean mayAccessAppointment(User user, Appointment appointment) {
        if (user.getRole() == User.Role.PATIENT) {
            Long myPatientId = patientService.getByUserId(user.getId()).map(Patient::getId).orElse(null);
            return myPatientId != null
                    && appointment.getPatient() != null
                    && myPatientId.equals(appointment.getPatient().getId());
        }
        if (user.getRole() == User.Role.DOCTOR) {
            Long myDoctorId = doctorService.getByUserId(user.getId()).map(Doctor::getId).orElse(null);
            return myDoctorId != null
                    && appointment.getDoctor() != null
                    && myDoctorId.equals(appointment.getDoctor().getId());
        }
        return true;
    }

    private AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse r = new AppointmentResponse();
        r.setId(a.getId());
        r.setDoctorId(a.getDoctor() != null ? a.getDoctor().getId() : null);
        r.setPatientId(a.getPatient() != null ? a.getPatient().getId() : null);
        r.setClinicId(a.getClinic() != null ? a.getClinic().getId() : null);
        r.setAppointmentTime(a.getAppointmentTime());
        r.setStatus(mapStatus(a.getStatus()));
        r.setAppointmentType(mapTypeToApi(a.getAppointmentType()));
        return r;
    }

    private static AppointmentStatus mapStatus(Appointment.Status s) {
        if (s == null) {
            return null;
        }
        return AppointmentStatus.valueOf(s.name());
    }

    private static com.medcare.api.model.AppointmentType mapTypeToApi(Appointment.AppointmentType t) {
        if (t == null) {
            return null;
        }
        return com.medcare.api.model.AppointmentType.valueOf(t.name());
    }

    private static Appointment.AppointmentType toEntityType(com.medcare.api.model.AppointmentType t) {
        if (t == null) {
            return null;
        }
        return Appointment.AppointmentType.valueOf(t.name());
    }
}
