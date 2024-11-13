package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;

import java.util.Optional;

public interface ReservationServiceOut {
    ReservationDTO createReservationOut(RequestReservation requestReservation);
    ReservationDTO updateReservationOut(Long reservationId, RequestReservation requestReservation);
    ReservationDTO deleteReservationOut(Long reservationId);
    Optional<ResponseReservation> findReservationByIdOut(Long reservationId);
    ResponseListPageableReservation listReservationByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir);
}
