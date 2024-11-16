package com.braidsbeautyByAngie.aggregates.response.workService;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseWorkServiceWithoutReservation {
    private List <ServiceDTO> serviceDTOList;
    private List <ReservationDTO> reservationDTOList;
}
