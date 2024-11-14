package com.braidsbeautyByAngie.aggregates.response.workService;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseWorkServiceWithoutReservation {
    private ResponseWorkService responseWorkService;
}
