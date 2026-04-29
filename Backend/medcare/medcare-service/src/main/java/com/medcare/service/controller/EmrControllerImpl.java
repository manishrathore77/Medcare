package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.EmrController}.
 */

import com.medcare.api.controller.EmrController;
import com.medcare.api.model.DiagnosisDto;
import com.medcare.api.model.PrescriptionDto;
import com.medcare.api.model.SoapNoteDto;
import com.medcare.api.model.TreatmentDto;
import com.medcare.service.entity.Appointment;
import com.medcare.service.entity.Diagnosis;
import com.medcare.service.entity.Prescription;
import com.medcare.service.entity.SoapNote;
import com.medcare.service.entity.Treatment;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.DiagnosisService;
import com.medcare.service.service.PrescriptionService;
import com.medcare.service.service.SoapNoteService;
import com.medcare.service.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class EmrControllerImpl implements EmrController {

    private final DiagnosisService diagnosisService;
    private final PrescriptionService prescriptionService;
    private final SoapNoteService soapNoteService;
    private final TreatmentService treatmentService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_READ')")
    public ResponseEntity<PagedResponse<DiagnosisDto>> listDiagnoses(int page, int size) {
        List<Diagnosis> all = diagnosisService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<DiagnosisDto> content = all.subList(from, to).stream().map(this::toDiagnosisDto).collect(Collectors.toList());
        PagedResponse<DiagnosisDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_WRITE')")
    public ResponseEntity<ApiResponse<DiagnosisDto>> createDiagnosis(DiagnosisDto dto) {
        Diagnosis entity = toDiagnosis(dto);
        Diagnosis saved = diagnosisService.createDiagnosis(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toDiagnosisDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_READ')")
    public ResponseEntity<PagedResponse<PrescriptionDto>> listPrescriptions(int page, int size) {
        List<Prescription> all = prescriptionService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<PrescriptionDto> content = all.subList(from, to).stream().map(this::toPrescriptionDto).collect(Collectors.toList());
        PagedResponse<PrescriptionDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_WRITE')")
    public ResponseEntity<ApiResponse<PrescriptionDto>> createPrescription(PrescriptionDto dto) {
        Prescription entity = toPrescription(dto);
        Prescription saved = prescriptionService.createPrescription(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toPrescriptionDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_READ')")
    public ResponseEntity<PagedResponse<SoapNoteDto>> listSoapNotes(int page, int size) {
        List<SoapNote> all = soapNoteService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<SoapNoteDto> content = all.subList(from, to).stream().map(this::toSoapNoteDto).collect(Collectors.toList());
        PagedResponse<SoapNoteDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_WRITE')")
    public ResponseEntity<ApiResponse<SoapNoteDto>> createSoapNote(SoapNoteDto dto) {
        SoapNote entity = toSoapNote(dto);
        SoapNote saved = soapNoteService.createSoapNote(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toSoapNoteDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_READ')")
    public ResponseEntity<PagedResponse<TreatmentDto>> listTreatments(int page, int size) {
        List<Treatment> all = treatmentService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<TreatmentDto> content = all.subList(from, to).stream().map(this::toTreatmentDto).collect(Collectors.toList());
        PagedResponse<TreatmentDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_EMR_WRITE')")
    public ResponseEntity<ApiResponse<TreatmentDto>> createTreatment(TreatmentDto dto) {
        Treatment entity = toTreatment(dto);
        Treatment saved = treatmentService.createTreatment(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toTreatmentDto(saved), HttpStatus.CREATED.value()));
    }

    private DiagnosisDto toDiagnosisDto(Diagnosis e) {
        DiagnosisDto d = new DiagnosisDto();
        d.setId(e.getId());
        d.setAppointmentId(e.getAppointment() != null ? e.getAppointment().getId() : null);
        d.setDiagnosisName(e.getDiagnosisName());
        d.setSeverity(e.getSeverity());
        d.setNotes(e.getNotes());
        return d;
    }

    private Diagnosis toDiagnosis(DiagnosisDto d) {
        Diagnosis e = new Diagnosis();
        e.setDiagnosisName(d.getDiagnosisName());
        e.setSeverity(d.getSeverity());
        e.setNotes(d.getNotes());
        if (d.getAppointmentId() != null) {
            Appointment a = new Appointment();
            a.setId(d.getAppointmentId());
            e.setAppointment(a);
        }
        return e;
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

    private Prescription toPrescription(PrescriptionDto d) {
        Prescription e = new Prescription();
        e.setMedicineName(d.getMedicineName());
        e.setDosage(d.getDosage());
        e.setFrequency(d.getFrequency());
        e.setDuration(d.getDuration());
        if (d.getAppointmentId() != null) {
            Appointment a = new Appointment();
            a.setId(d.getAppointmentId());
            e.setAppointment(a);
        }
        return e;
    }

    private SoapNoteDto toSoapNoteDto(SoapNote e) {
        SoapNoteDto d = new SoapNoteDto();
        d.setId(e.getId());
        d.setAppointmentId(e.getAppointment() != null ? e.getAppointment().getId() : null);
        d.setSubjective(e.getSubjective());
        d.setObjective(e.getObjective());
        d.setAssessment(e.getAssessment());
        d.setPlan(e.getPlan());
        return d;
    }

    private SoapNote toSoapNote(SoapNoteDto d) {
        SoapNote e = new SoapNote();
        e.setSubjective(d.getSubjective());
        e.setObjective(d.getObjective());
        e.setAssessment(d.getAssessment());
        e.setPlan(d.getPlan());
        if (d.getAppointmentId() != null) {
            Appointment a = new Appointment();
            a.setId(d.getAppointmentId());
            e.setAppointment(a);
        }
        return e;
    }

    private TreatmentDto toTreatmentDto(Treatment e) {
        TreatmentDto d = new TreatmentDto();
        d.setId(e.getId());
        d.setAppointmentId(e.getAppointment() != null ? e.getAppointment().getId() : null);
        d.setTreatmentName(e.getTreatmentName());
        d.setStartDate(e.getStartDate());
        d.setEndDate(e.getEndDate());
        d.setInstructions(e.getInstructions());
        return d;
    }

    private Treatment toTreatment(TreatmentDto d) {
        Treatment e = new Treatment();
        e.setTreatmentName(d.getTreatmentName());
        e.setStartDate(d.getStartDate());
        e.setEndDate(d.getEndDate());
        e.setInstructions(d.getInstructions());
        if (d.getAppointmentId() != null) {
            Appointment a = new Appointment();
            a.setId(d.getAppointmentId());
            e.setAppointment(a);
        }
        return e;
    }
}
