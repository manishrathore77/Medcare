package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.AuditLogController}.
 */

import com.medcare.api.controller.AuditLogController;
import com.medcare.api.model.AuditLogDto;
import com.medcare.service.entity.AuditLog;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuditLogControllerImpl implements AuditLogController {

    private final AuditLogService auditLogService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_AUDIT_LOGS_READ')")
    public ResponseEntity<PagedResponse<AuditLogDto>> list(int page, int size) {
        List<AuditLog> all = auditLogService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<AuditLogDto> content = all.subList(from, to).stream().map(this::toDto).collect(Collectors.toList());
        PagedResponse<AuditLogDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_AUDIT_LOGS_READ')")
    public ResponseEntity<ApiResponse<AuditLogDto>> get(Long id) {
        return auditLogService.getById(id)
                .map(a -> ResponseEntity.ok(new ApiResponse<>(true, "OK", toDto(a), HttpStatus.OK.value())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Not found", null, HttpStatus.NOT_FOUND.value())));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_AUDIT_LOGS_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        auditLogService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    private AuditLogDto toDto(AuditLog a) {
        AuditLogDto d = new AuditLogDto();
        d.setId(a.getId());
        d.setUserId(a.getUser() != null ? a.getUser().getId() : null);
        d.setAction(a.getAction());
        d.setEntityName(a.getEntityName());
        d.setEntityId(a.getEntityId());
        d.setOldValue(a.getOldValue());
        d.setNewValue(a.getNewValue());
        return d;
    }
}
