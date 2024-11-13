package com.braidsbeautyByAngie.aggregates.response.reservations;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkServiceWithoutReservation;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseReservation {
    private ReservationDTO reservationDTO;
    private List <ResponseWorkServiceWithoutReservation> responseWorkServiceWithoutReservationList;
}
