package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.ports.in.ReservationServiceIn;
import com.braidsbeautyByAngie.ports.out.ReservationServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationServiceIn{

    private final ReservationServiceOut reservationServiceOut;

    @Override
    public ReservationDTO createReservationIn(RequestReservation requestReservation) {
        return reservationServiceOut.createReservationOut(requestReservation);
    }

    @Override
    public ReservationDTO updateReservationIn(Long reservationId, RequestReservation requestReservation) {
        return reservationServiceOut.updateReservationOut(reservationId, requestReservation);
    }

    @Override
    public ReservationDTO deleteReservationIn(Long reservationId) {
        return reservationServiceOut.deleteReservationOut(reservationId);
    }

    @Override
    public Optional<ResponseReservation> findReservationByIdIn(Long reservationId) {
        return reservationServiceOut.findReservationByIdOut(reservationId);
    }

    @Override
    public ResponseListPageableReservation listReservationByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return reservationServiceOut.listReservationByPageOut(pageNumber, pageSize, orderBy, sortDir);
    }
}
