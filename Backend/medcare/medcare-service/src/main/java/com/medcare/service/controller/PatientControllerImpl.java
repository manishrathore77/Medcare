package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.PatientController}.
 */

import com.medcare.api.controller.PatientController;
import com.medcare.api.model.PatientRequest;
import com.medcare.api.model.PatientResponse;
import com.medcare.service.entity.Patient;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PatientControllerImpl implements PatientController {

    private final PatientService patientService;

    @Override
    @PreAuthorize("hasAuthority('PATIENT_MANAGEMENT_PATIENTS_READ')")
    public ResponseEntity<PagedResponse<PatientResponse>> list(int page, int size) {
        List<Patient> all = patientService.getAllPatients();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<PatientResponse> content = all.subList(from, to).stream().map(this::toResponse).collect(Collectors.toList());
        PagedResponse<PatientResponse> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('PATIENT_MANAGEMENT_PATIENTS_READ')")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(Long id) {
        return patientService.getById(id)
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toResponse(p), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('PATIENT_MANAGEMENT_PATIENTS_WRITE')")
    public ResponseEntity<ApiResponse<PatientResponse>> create(PatientRequest req) {
        Patient p = new Patient();
        p.setFirstName(req.getFirstName());
        p.setLastName(req.getLastName());
        p.setGender(req.getGender());
        p.setDob(req.getDob());
        p.setAddress(req.getAddress());
        p.setEmergencyContact(req.getEmergencyContact());
        p.setInsuranceProvider(req.getInsuranceProvider());
        p.setInsuranceNumber(req.getInsuranceNumber());
        Patient saved = patientService.registerPatient(p);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toResponse(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('PATIENT_MANAGEMENT_PATIENTS_WRITE')")
    public ResponseEntity<ApiResponse<PatientResponse>> update(Long id, PatientRequest req) {
        Patient p = new Patient();
        p.setFirstName(req.getFirstName());
        p.setLastName(req.getLastName());
        p.setGender(req.getGender());
        p.setDob(req.getDob());
        p.setAddress(req.getAddress());
        p.setEmergencyContact(req.getEmergencyContact());
        p.setInsuranceProvider(req.getInsuranceProvider());
        p.setInsuranceNumber(req.getInsuranceNumber());
        Patient updated = patientService.updatePatient(id, p);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toResponse(updated), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('PATIENT_MANAGEMENT_PATIENTS_WRITE')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
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
}
