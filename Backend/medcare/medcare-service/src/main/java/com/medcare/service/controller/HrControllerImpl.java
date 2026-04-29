package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.HrController}.
 */

import com.medcare.api.controller.HrController;
import com.medcare.api.model.AttendanceDto;
import com.medcare.api.model.PayrollDto;
import com.medcare.api.model.StaffDto;
import com.medcare.service.entity.Attendance;
import com.medcare.service.entity.Payroll;
import com.medcare.service.entity.Staff;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.AttendanceService;
import com.medcare.service.service.PayrollService;
import com.medcare.service.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HrControllerImpl implements HrController {

    private final StaffService staffService;
    private final AttendanceService attendanceService;
    private final PayrollService payrollService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_HR_READ')")
    public ResponseEntity<PagedResponse<StaffDto>> listStaff(int page, int size) {
        List<Staff> all = staffService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<StaffDto> content = all.subList(from, to).stream().map(this::toStaffDto).collect(Collectors.toList());
        PagedResponse<StaffDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_HR_WRITE')")
    public ResponseEntity<ApiResponse<StaffDto>> createStaff(StaffDto dto) {
        Staff entity = toStaff(dto);
        Staff saved = staffService.createStaff(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toStaffDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_HR_READ')")
    public ResponseEntity<PagedResponse<AttendanceDto>> listAttendance(int page, int size) {
        List<Attendance> all = attendanceService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<AttendanceDto> content = all.subList(from, to).stream().map(this::toAttendanceDto).collect(Collectors.toList());
        PagedResponse<AttendanceDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_HR_WRITE')")
    public ResponseEntity<ApiResponse<AttendanceDto>> createAttendance(AttendanceDto dto) {
        Attendance entity = toAttendance(dto);
        Attendance saved = attendanceService.createAttendance(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toAttendanceDto(saved), HttpStatus.CREATED.value()));
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_HR_READ')")
    public ResponseEntity<PagedResponse<PayrollDto>> listPayroll(int page, int size) {
        List<Payroll> all = payrollService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<PayrollDto> content = all.subList(from, to).stream().map(this::toPayrollDto).collect(Collectors.toList());
        PagedResponse<PayrollDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_HR_WRITE')")
    public ResponseEntity<ApiResponse<PayrollDto>> createPayroll(PayrollDto dto) {
        Payroll entity = toPayroll(dto);
        Payroll saved = payrollService.createPayroll(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toPayrollDto(saved), HttpStatus.CREATED.value()));
    }

    private StaffDto toStaffDto(Staff e) {
        StaffDto d = new StaffDto();
        d.setId(e.getId());
        d.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        d.setName(e.getName());
        d.setDepartment(e.getDepartment());
        d.setRole(e.getRole());
        d.setSalary(e.getSalary());
        d.setActive(e.getIsActive());
        return d;
    }

    private Staff toStaff(StaffDto d) {
        Staff e = new Staff();
        e.setName(d.getName());
        e.setDepartment(d.getDepartment());
        e.setRole(d.getRole());
        e.setSalary(d.getSalary());
        e.setIsActive(d.getActive() != null ? d.getActive() : true);
        if (d.getUserId() != null) {
            User u = new User();
            u.setId(d.getUserId());
            e.setUser(u);
        }
        return e;
    }

    private AttendanceDto toAttendanceDto(Attendance e) {
        AttendanceDto d = new AttendanceDto();
        d.setId(e.getId());
        d.setStaffId(e.getStaff() != null ? e.getStaff().getId() : null);
        d.setDate(e.getDate());
        d.setCheckIn(e.getCheckIn());
        d.setCheckOut(e.getCheckOut());
        return d;
    }

    private Attendance toAttendance(AttendanceDto d) {
        Attendance e = new Attendance();
        e.setDate(d.getDate());
        e.setCheckIn(d.getCheckIn());
        e.setCheckOut(d.getCheckOut());
        if (d.getStaffId() != null) {
            Staff s = new Staff();
            s.setId(d.getStaffId());
            e.setStaff(s);
        }
        return e;
    }

    private PayrollDto toPayrollDto(Payroll e) {
        PayrollDto d = new PayrollDto();
        d.setId(e.getId());
        d.setStaffId(e.getStaff() != null ? e.getStaff().getId() : null);
        d.setMonth(e.getMonth());
        d.setBaseSalary(e.getBaseSalary());
        d.setDeductions(e.getDeductions());
        d.setNetSalary(e.getNetSalary());
        d.setPaidOn(e.getPaidOn());
        return d;
    }

    private Payroll toPayroll(PayrollDto d) {
        Payroll e = new Payroll();
        e.setMonth(d.getMonth());
        e.setBaseSalary(d.getBaseSalary());
        e.setDeductions(d.getDeductions());
        e.setNetSalary(d.getNetSalary());
        e.setPaidOn(d.getPaidOn());
        if (d.getStaffId() != null) {
            Staff s = new Staff();
            s.setId(d.getStaffId());
            e.setStaff(s);
        }
        return e;
    }
}
