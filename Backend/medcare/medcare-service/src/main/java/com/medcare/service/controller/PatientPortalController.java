package com.medcare.service.controller;

import com.medcare.api.model.PatientRequest;
import com.medcare.api.model.PatientResponse;
import com.medcare.api.model.PrescriptionDto;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.Prescription;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.service.PatientService;
import com.medcare.service.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Patient self-service: profile and prescriptions scoped to the logged-in {@code PATIENT} user.
 */
@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientPortalController {

    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> getMe() {
        User user = currentUser();
        return patientService.getByUserId(user.getId())
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toResponse(p), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Patient profile not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> updateMe(@Valid @RequestBody PatientRequest req) {
        User user = currentUser();
        Patient existing = patientService.getByUserId(user.getId()).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Patient profile not found", null, HttpStatus.NOT_FOUND.value()));
        }
        existing.setFirstName(req.getFirstName());
        existing.setLastName(req.getLastName());
        existing.setGender(req.getGender());
        existing.setDob(req.getDob());
        existing.setAddress(req.getAddress());
        existing.setEmergencyContact(req.getEmergencyContact());
        existing.setInsuranceProvider(req.getInsuranceProvider());
        existing.setInsuranceNumber(req.getInsuranceNumber());
        Patient saved = patientService.updatePatient(existing.getId(), existing);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toResponse(saved), HttpStatus.OK.value()));
    }

    @GetMapping("/prescriptions")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<PrescriptionDto>>> myPrescriptions() {
        User user = currentUser();
        Long patientId = patientService.getByUserId(user.getId()).map(Patient::getId).orElseThrow();
        List<PrescriptionDto> list = prescriptionService.listForPatient(patientId).stream()
                .map(this::toPrescriptionDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", list, HttpStatus.OK.value()));
    }

    private User currentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow();
    }

    private PatientResponse toResponse(Patient p) {
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

    private PrescriptionDto toPrescriptionDto(Prescription e) {
        PrescriptionDto d = new PrescriptionDto();
        d.setId(e.getId());
        d.setAppointmentId(e.getAppointment() != null ? e.getAppointment().getId() : null);
        d.setMedicineName(e.getMedicineName());
        d.setDosage(e.getDosage());
        d.setFrequency(e.getFrequency());
        d.setDuration(e.getDuration());
        return d;
    }
}
