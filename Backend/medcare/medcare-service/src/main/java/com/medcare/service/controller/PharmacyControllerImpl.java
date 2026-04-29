package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.PharmacyController}.
 */

import com.medcare.api.controller.PharmacyController;
import com.medcare.api.model.InventoryChangeType;
import com.medcare.api.model.InventoryLogDto;
import com.medcare.api.model.MedicineDto;
import com.medcare.service.entity.InventoryLog;
import com.medcare.service.entity.Medicine;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.generic.exception.ResourceNotFoundException;
import com.medcare.service.service.InventoryLogService;
import com.medcare.service.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PharmacyControllerImpl implements PharmacyController {

    private final MedicineService medicineService;
    private final InventoryLogService inventoryLogService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_READ')")
    public ResponseEntity<PagedResponse<MedicineDto>> listMedicines(int page, int size) {
        List<Medicine> all = medicineService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<MedicineDto> content = all.subList(from, to).stream().map(this::toMedicineDto).collect(Collectors.toList());
        PagedResponse<MedicineDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_WRITE')")
    public ResponseEntity<ApiResponse<MedicineDto>> createMedicine(MedicineDto dto) {
        Medicine entity = toMedicine(dto);
        Medicine saved = medicineService.createMedicine(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toMedicineDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_READ')")
    public ResponseEntity<ApiResponse<MedicineDto>> getMedicine(Long id) {
        Medicine m = medicineService.getById(id).orElseThrow(() -> new ResourceNotFoundException("Medicine", id));
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", toMedicineDto(m), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_WRITE')")
    public ResponseEntity<ApiResponse<MedicineDto>> updateMedicine(Long id, MedicineDto dto) {
        Medicine patch = toMedicine(dto);
        Medicine saved = medicineService.updateMedicine(id, patch);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated", toMedicineDto(saved), HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_WRITE')")
    public ResponseEntity<ApiResponse<Object>> deleteMedicine(Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted", null, HttpStatus.OK.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_READ')")
    public ResponseEntity<PagedResponse<InventoryLogDto>> listInventory(int page, int size) {
        List<InventoryLog> all = inventoryLogService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<InventoryLogDto> content = all.subList(from, to).stream().map(this::toInventoryLogDto).collect(Collectors.toList());
        PagedResponse<InventoryLogDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_PHARMACY_WRITE')")
    public ResponseEntity<ApiResponse<InventoryLogDto>> createInventoryLog(InventoryLogDto dto) {
        InventoryLog entity = toInventoryLog(dto);
        InventoryLog saved = inventoryLogService.createInventoryLog(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toInventoryLogDto(saved), HttpStatus.CREATED.value()));
    }

    private MedicineDto toMedicineDto(Medicine e) {
        MedicineDto d = new MedicineDto();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setBatchNo(e.getBatchNo());
        d.setExpiryDate(e.getExpiryDate());
        d.setStockQuantity(e.getStockQuantity());
        d.setReorderLevel(e.getReorderLevel());
        d.setUnitPrice(e.getUnitPrice());
        return d;
    }

    private Medicine toMedicine(MedicineDto d) {
        Medicine e = new Medicine();
        e.setName(d.getName());
        e.setBatchNo(d.getBatchNo());
        e.setExpiryDate(d.getExpiryDate());
        e.setStockQuantity(d.getStockQuantity());
        e.setReorderLevel(d.getReorderLevel() != null ? d.getReorderLevel() : 10);
        e.setUnitPrice(d.getUnitPrice());
        return e;
    }

    private InventoryLogDto toInventoryLogDto(InventoryLog e) {
        InventoryLogDto d = new InventoryLogDto();
        d.setId(e.getId());
        d.setMedicineId(e.getMedicine() != null ? e.getMedicine().getId() : null);
        d.setChangeType(e.getChangeType() != null ? InventoryChangeType.valueOf(e.getChangeType().name()) : null);
        d.setQuantity(e.getQuantity());
        d.setReason(e.getReason());
        return d;
    }

    private InventoryLog toInventoryLog(InventoryLogDto d) {
        InventoryLog e = new InventoryLog();
        e.setChangeType(d.getChangeType() != null ? InventoryLog.ChangeType.valueOf(d.getChangeType().name()) : null);
        e.setQuantity(d.getQuantity());
        e.setReason(d.getReason());
        if (d.getMedicineId() != null) {
            Medicine m = new Medicine();
            m.setId(d.getMedicineId());
            e.setMedicine(m);
        }
        return e;
    }
}
