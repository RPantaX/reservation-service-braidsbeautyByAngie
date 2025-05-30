package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservationDetail;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ReservationCore;

import java.util.List;
import java.util.Optional;

public interface ReservationServiceOut {
    ReservationDTO createReservationOut(List<RequestReservation> requestReservationList);
    ReservationDTO updateReservationOut(Long reservationId, List<RequestReservation> requestReservationList);
    ReservationDTO deleteReservationOut(Long reservationId);
    ResponseReservationDetail findReservationByIdOut(Long reservationId);
    ResponseListPageableReservation listReservationByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir);
    ReservationCore reserveReservationOut(Long shopOrderId, Long reservationId);
    void cancelReservationOut(Long reservationId);
}
