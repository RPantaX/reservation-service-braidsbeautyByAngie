package com.braidsbeautyByAngie.aggregates.response.workService;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseWorkService {
    private ServiceDTO serviceDTO;
    private ScheduleDTO scheduleDTO;
    private ReservationDTO reservationDTO;
}
