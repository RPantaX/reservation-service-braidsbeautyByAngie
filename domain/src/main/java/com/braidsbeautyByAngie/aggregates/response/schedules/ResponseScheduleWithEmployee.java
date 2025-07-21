package com.braidsbeautyByAngie.aggregates.response.schedules;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.dto.rest.EmployeeDto;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseScheduleWithEmployee {
    private ScheduleDTO scheduleDTO;
    private EmployeeDto employeeDto;
}
