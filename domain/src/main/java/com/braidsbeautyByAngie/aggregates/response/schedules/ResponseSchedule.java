package com.braidsbeautyByAngie.aggregates.response.schedules;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkServiceWithoutReservation;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseSchedule {
    private ScheduleDTO scheduleDTO;
    private ResponseWorkServiceWithoutReservation responseWorkServiceWithoutReservation;
}
