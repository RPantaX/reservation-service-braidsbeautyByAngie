package com.braidsbeautyByAngie.aggregates.response.reservations;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseWorkServiceDetail {
    private ServiceDTO serviceDTO;
    private ScheduleDTO scheduleDTO;
}
