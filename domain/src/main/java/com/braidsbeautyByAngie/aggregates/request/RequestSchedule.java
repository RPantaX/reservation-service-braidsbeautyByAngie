package com.braidsbeautyByAngie.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RequestSchedule {
    private LocalDate scheduleDate;
    private LocalTime scheduleHourStart;
    private LocalTime scheduleHourEnd;
}
