package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservationDetail;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ReservationCore;

import java.util.List;
import java.util.Optional;


public interface ReservationServiceIn {
    ReservationDTO createReservationIn(List<RequestReservation> requestReservationList);
    ReservationDTO updateReservationIn(Long reservationId, List<RequestReservation> requestReservationList);
    ReservationDTO deleteReservationIn(Long reservationId);
    ResponseReservationDetail findReservationByIdIn(Long reservationId);
    ResponseListPageableReservation listReservationByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir);
    ReservationCore reserveReservationIn(Long shopOrderId, Long reservationId);
    void cancelReservationIn(Long reservationId);
}
