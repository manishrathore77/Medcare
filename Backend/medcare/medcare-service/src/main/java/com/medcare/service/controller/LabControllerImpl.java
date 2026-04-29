package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.LabController}.
 */

import com.medcare.api.controller.LabController;
import com.medcare.api.model.LabReportDto;
import com.medcare.api.model.LabTestDto;
import com.medcare.api.model.LabTestStatus;
import com.medcare.service.entity.Appointment;
import com.medcare.service.entity.LabReport;
import com.medcare.service.entity.LabTest;
import com.medcare.service.entity.Patient;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.repository.UserRepository;
import com.medcare.service.service.LabReportService;
import com.medcare.service.service.LabTestService;
import com.medcare.service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LabControllerImpl implements LabController {

    private final LabTestService labTestService;
    private final LabReportService labReportService;
    private final PatientService patientService;
    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_LAB_READ')")
    public ResponseEntity<PagedResponse<LabTestDto>> listTests(int page, int size) {
        List<LabTest> all = labTestService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<LabTestDto> content = all.subList(from, to).stream().map(this::toLabTestDto).collect(Collectors.toList());
        PagedResponse<LabTestDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_LAB_WRITE')")
    public ResponseEntity<ApiResponse<LabTestDto>> createTest(LabTestDto dto) {
        LabTest entity = toLabTest(dto);
        LabTest saved = labTestService.createLabTest(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toLabTestDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_LAB_READ')")
    public ResponseEntity<PagedResponse<LabReportDto>> listReports(int page, Long patientId, int size) {
        Long effectivePatientId = resolvePatientScope(patientId);
        List<LabReport> all = effectivePatientId != null
                ? labReportService.getAllByPatientId(effectivePatientId)
                : labReportService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<LabReportDto> content = all.subList(from, to).stream().map(this::toLabReportDto).collect(Collectors.toList());
        PagedResponse<LabReportDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_LAB_WRITE')")
    public ResponseEntity<ApiResponse<LabReportDto>> createReport(LabReportDto dto) {
        LabReport entity = toLabReport(dto);
        LabReport saved = labReportService.createLabReport(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toLabReportDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_LAB_WRITE')")
    public ResponseEntity<ApiResponse<LabReportDto>> uploadReport(Long patientId, Long labTestId, @RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "File is required", null, HttpStatus.BAD_REQUEST.value()));
        }
        Patient patient = patientService.getById(patientId).orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Patient not found", null, HttpStatus.NOT_FOUND.value()));
        }
        LabReport report = new LabReport();
        report.setPatient(patient);
        report.setReportFileUrl("/api/lab/reports/pending/download");
        report.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "report");
        report.setContentType(file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);
        try {
            report.setFileData(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Could not read file", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (labTestId != null) {
            LabTest lt = new LabTest();
            lt.setId(labTestId);
            report.setLabTest(lt);
        }
        LabReport saved = labReportService.createLabReport(report);
        saved.setReportFileUrl("/api/lab/reports/" + saved.getId() + "/download");
        saved = labReportService.createLabReport(saved);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Uploaded", toLabReportDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_LAB_READ')")
    public ResponseEntity<?> downloadReport(Long reportId) {
        LabReport report = labReportService.getById(reportId).orElse(null);
        if (report == null || report.getFileData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Report not found", null, HttpStatus.NOT_FOUND.value()));
        }
        Long effectivePatientId = resolvePatientScope(null);
        if (effectivePatientId != null && (report.getPatient() == null || !effectivePatientId.equals(report.getPatient().getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Not allowed", null, HttpStatus.FORBIDDEN.value()));
        }
        String fileName = report.getFileName() != null ? report.getFileName() : ("lab-report-" + reportId);
        String contentType = report.getContentType() != null ? report.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(report.getFileData()));
    }

    private LabTestDto toLabTestDto(LabTest e) {
        LabTestDto d = new LabTestDto();
        d.setId(e.getId());
        d.setAppointmentId(e.getAppointment() != null ? e.getAppointment().getId() : null);
        d.setTestName(e.getTestName());
        d.setNormalRange(e.getNormalRange());
        d.setResultValue(e.getResultValue());
        d.setStatus(e.getStatus() != null ? LabTestStatus.valueOf(e.getStatus().name()) : null);
        return d;
    }

    private LabTest toLabTest(LabTestDto d) {
        LabTest e = new LabTest();
        e.setTestName(d.getTestName());
        e.setNormalRange(d.getNormalRange());
        e.setResultValue(d.getResultValue());
        e.setStatus(d.getStatus() != null ? LabTest.Status.valueOf(d.getStatus().name()) : null);
        if (d.getAppointmentId() != null) {
            Appointment a = new Appointment();
            a.setId(d.getAppointmentId());
            e.setAppointment(a);
        }
        return e;
    }

    private LabReportDto toLabReportDto(LabReport e) {
        LabReportDto d = new LabReportDto();
        d.setId(e.getId());
        d.setLabTestId(e.getLabTest() != null ? e.getLabTest().getId() : null);
        d.setPatientId(e.getPatient() != null ? e.getPatient().getId() : null);
        d.setReportFileUrl(e.getReportFileUrl());
        d.setFileName(e.getFileName());
        d.setContentType(e.getContentType());
        d.setUploadedAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null);
        return d;
    }

    private LabReport toLabReport(LabReportDto d) {
        LabReport e = new LabReport();
        e.setReportFileUrl(d.getReportFileUrl());
        e.setFileName(d.getFileName());
        e.setContentType(d.getContentType());
        if (d.getLabTestId() != null) {
            LabTest lt = new LabTest();
            lt.setId(d.getLabTestId());
            e.setLabTest(lt);
        }
        if (d.getPatientId() != null) {
            Patient p = new Patient();
            p.setId(d.getPatientId());
            e.setPatient(p);
        }
        return e;
    }

    private Long resolvePatientScope(Long requestedPatientId) {
        User user = currentUser();
        if (user.getRole() == User.Role.PATIENT) {
            return patientService.getByUserId(user.getId()).map(Patient::getId).orElse(null);
        }
        return requestedPatientId;
    }

    private User currentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(name).orElseThrow();
    }
}
