package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservationDetail;
import com.braidsbeautyByAngie.ports.in.ReservationServiceIn;
import com.braidsbeautyByAngie.ports.out.ReservationServiceOut;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ReservationCore;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ServiceCore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationServiceIn{

    private final ReservationServiceOut reservationServiceOut;

    @Override
    public ReservationDTO createReservationIn(List<RequestReservation> requestReservationList) {
        return reservationServiceOut.createReservationOut(requestReservationList);
    }

    @Override
    public ReservationDTO updateReservationIn(Long reservationId, List<RequestReservation> requestReservationList) {
        return reservationServiceOut.updateReservationOut(reservationId, requestReservationList);
    }

    @Override
    public ReservationDTO deleteReservationIn(Long reservationId) {
        return reservationServiceOut.deleteReservationOut(reservationId);
    }

    @Override
    public ResponseReservationDetail findReservationByIdIn(Long reservationId) {
        return reservationServiceOut.findReservationByIdOut(reservationId);
    }

    @Override
    public ResponseListPageableReservation listReservationByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return reservationServiceOut.listReservationByPageOut(pageNumber, pageSize, orderBy, sortDir);
    }

    @Override
    public ReservationCore reserveReservationIn(Long shopOrderId, Long reservationId) {
        return reservationServiceOut.reserveReservationOut(shopOrderId, reservationId);
    }

    @Override
    public void cancelReservationIn(Long reservationId) {
        reservationServiceOut.cancelReservationOut(reservationId);
    }
}
