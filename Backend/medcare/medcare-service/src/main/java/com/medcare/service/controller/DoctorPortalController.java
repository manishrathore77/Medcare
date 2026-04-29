package com.medcare.service.controller;

import com.medcare.api.model.DoctorRequest;
import com.medcare.api.model.DoctorResponse;
import com.medcare.api.model.PatientResponse;
import com.medcare.service.entity.Doctor;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.service.DoctorService;
import com.medcare.service.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Doctor self-service: profile and patients seen in their appointments.
 */
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorPortalController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getMe() {
        User user = currentUser();
        return doctorService.getByUserId(user.getId())
                .map(d -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toDoctorResponse(d), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Doctor profile not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateMe(@Valid @RequestBody DoctorRequest req) {
        User user = currentUser();
        Doctor existing = doctorService.getByUserId(user.getId()).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Doctor profile not found", null, HttpStatus.NOT_FOUND.value()));
        }
        existing.setFirstName(req.getFirstName());
        existing.setLastName(req.getLastName());
        existing.setSpecialty(req.getSpecialty());
        existing.setLicenseNumber(req.getLicenseNumber());
        existing.setContactNumber(req.getContactNumber());
        existing.setEmail(req.getEmail());
        Doctor saved = doctorService.updateDoctor(existing.getId(), existing);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toDoctorResponse(saved), HttpStatus.OK.value()));
    }

    @GetMapping("/patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> myPatients() {
        User user = currentUser();
        Long doctorId = doctorService.getByUserId(user.getId()).map(Doctor::getId).orElseThrow();
        List<PatientResponse> list = patientService.findPatientsForDoctor(doctorId).stream()
                .map(this::toPatientResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", list, HttpStatus.OK.value()));
    }

    @GetMapping("/patients/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PatientResponse>> myPatientById(@PathVariable Long id) {
        User user = currentUser();
        Long doctorId = doctorService.getByUserId(user.getId()).map(Doctor::getId).orElseThrow();
        boolean allowed = patientService.findPatientsForDoctor(doctorId).stream()
                .anyMatch(p -> id.equals(p.getId()));
        if (!allowed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value()));
        }
        return patientService.getById(id)
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toPatientResponse(p), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    private User currentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow();
    }

    private DoctorResponse toDoctorResponse(Doctor d) {
        DoctorResponse r = new DoctorResponse();
        r.setId(d.getId());
        r.setFirstName(d.getFirstName());
        r.setLastName(d.getLastName());
        r.setSpecialty(d.getSpecialty());
        r.setLicenseNumber(d.getLicenseNumber());
        r.setContactNumber(d.getContactNumber());
        r.setEmail(d.getEmail());
        r.setActive(d.getIsActive());
        return r;
    }

    private PatientResponse toPatientResponse(Patient p) {
        PatientResponse r = new PatientResponse();
        r.setId(p.getId());
        r.setFirstName(p.getFirstName());
        r.setLastName(p.getLastName());
        r.setGender(p.getGender());
        r.setDob(p.getDob());
        r.setAddress(p.getAddress());
        r.setEmergencyContact(p.getEmergencyContact());
        r.setInsuranceProvider(p.getInsuranceProvider());
        r.setInsuranceNumber(p.getInsuranceNumber());
        return r;
    }
}
