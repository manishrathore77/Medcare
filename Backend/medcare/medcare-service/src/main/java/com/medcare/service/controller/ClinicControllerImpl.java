package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.ClinicController}.
 */

import com.medcare.api.controller.ClinicController;
import com.medcare.api.model.ClinicRequest;
import com.medcare.api.model.ClinicResponse;
import com.medcare.service.entity.Clinic;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ClinicControllerImpl implements ClinicController {

    private final ClinicService clinicService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_CLINICS_READ')")
    public ResponseEntity<PagedResponse<ClinicResponse>> list(int page, int size) {
        List<Clinic> all = clinicService.getAllClinics();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<ClinicResponse> content = all.subList(from, to).stream().map(this::toResponse).collect(Collectors.toList());
        PagedResponse<ClinicResponse> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_CLINICS_READ')")
    public ResponseEntity<ApiResponse<ClinicResponse>> getById(Long id) {
        return clinicService.getById(id)
                .map(c -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toResponse(c), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_CLINICS_WRITE')")
    public ResponseEntity<ApiResponse<ClinicResponse>> create(ClinicRequest req) {
        Clinic c = new Clinic();
        c.setName(req.getName());
        c.setLocation(req.getLocation());
        c.setContactNumber(req.getContactNumber());
        Clinic saved = clinicService.createClinic(c);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toResponse(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_CLINICS_WRITE')")
    public ResponseEntity<ApiResponse<ClinicResponse>> update(Long id, ClinicRequest req) {
        Clinic c = new Clinic();
        c.setName(req.getName());
        c.setLocation(req.getLocation());
        c.setContactNumber(req.getContactNumber());
        Clinic updated = clinicService.updateClinic(id, c);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toResponse(updated), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_CLINICS_WRITE')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    private ClinicResponse toResponse(Clinic c) {
        ClinicResponse r = new ClinicResponse();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setLocation(c.getLocation());
        r.setContactNumber(c.getContactNumber());
        return r;
    }
}
