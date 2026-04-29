package com.medcare.service.service;

import com.medcare.service.entity.Attendance;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for staff attendance entries.
 */
public interface AttendanceService {

    /**
     * @return all attendance records
     */
    List<Attendance> getAll();

    /**
     * @param id attendance id
     * @return optional attendance when found
     */
    Optional<Attendance> getById(Long id);

    /**
     * @param attendance new attendance row
     * @return persisted attendance
     */
    Attendance createAttendance(Attendance attendance);
}
