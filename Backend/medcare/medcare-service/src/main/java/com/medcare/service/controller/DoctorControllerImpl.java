package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.DoctorController}.
 */

import com.medcare.api.controller.DoctorController;
import com.medcare.api.model.DoctorRequest;
import com.medcare.api.model.DoctorResponse;
import com.medcare.service.entity.Doctor;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DoctorControllerImpl implements DoctorController {

    private final DoctorService doctorService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_DOCTORS_READ')")
    public ResponseEntity<PagedResponse<DoctorResponse>> list(int page, int size) {
        List<Doctor> all = doctorService.getActiveDoctors();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<DoctorResponse> content = all.subList(from, to).stream().map(this::toResponse).collect(Collectors.toList());
        PagedResponse<DoctorResponse> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_DOCTORS_READ')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getById(Long id) {
        return doctorService.getById(id)
                .map(d -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toResponse(d), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_DOCTORS_WRITE')")
    public ResponseEntity<ApiResponse<DoctorResponse>> create(DoctorRequest req) {
        Doctor d = new Doctor();
        d.setFirstName(req.getFirstName());
        d.setLastName(req.getLastName());
        d.setSpecialty(req.getSpecialty());
        d.setLicenseNumber(req.getLicenseNumber());
        d.setContactNumber(req.getContactNumber());
        d.setEmail(req.getEmail());
        Doctor saved = doctorService.createDoctor(d);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toResponse(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_DOCTORS_WRITE')")
    public ResponseEntity<ApiResponse<DoctorResponse>> update(Long id, DoctorRequest req) {
        Doctor d = new Doctor();
        d.setFirstName(req.getFirstName());
        d.setLastName(req.getLastName());
        d.setSpecialty(req.getSpecialty());
        d.setLicenseNumber(req.getLicenseNumber());
        d.setContactNumber(req.getContactNumber());
        d.setEmail(req.getEmail());
        Doctor updated = doctorService.updateDoctor(id, d);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toResponse(updated), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_DOCTORS_WRITE')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    private DoctorResponse toResponse(Doctor d) {
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
}
