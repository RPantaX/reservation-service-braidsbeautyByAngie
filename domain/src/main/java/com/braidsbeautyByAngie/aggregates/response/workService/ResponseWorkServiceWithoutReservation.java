package com.braidsbeautyByAngie.aggregates.response.workService;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseWorkServiceWithoutReservation {
    private WorkServiceDTO workServiceDTO;
    private ServiceDTO serviceDTO;
}
