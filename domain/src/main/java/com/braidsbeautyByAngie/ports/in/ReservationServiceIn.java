package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;

import java.util.List;
import java.util.Optional;


public interface ReservationServiceIn {
    ReservationDTO createReservationIn(List<RequestReservation> requestReservationList);
    ReservationDTO updateReservationIn(Long reservationId, RequestReservation requestReservation);
    ReservationDTO deleteReservationIn(Long reservationId);
    Optional<ResponseReservation> findReservationByIdIn(Long reservationId);
    ResponseListPageableReservation listReservationByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir);
}
