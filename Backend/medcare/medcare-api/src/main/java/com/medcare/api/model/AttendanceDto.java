package com.medcare.api.model;


/**
 * Data transfer object for Attendance in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    private Long id;
    private Long staffId;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
}
