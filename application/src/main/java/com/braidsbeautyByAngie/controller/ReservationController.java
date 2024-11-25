package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservationDetail;
import com.braidsbeautyByAngie.ports.in.ReservationServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@OpenAPIDefinition(
        info = @Info(
                title = "API-CATEGORY",
                version = "1.0",
                description = "Reservation management"
        )
)
@RestController
@RequestMapping("/v1/reservation-service/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceIn reservationService;

    @Operation(summary = "List all reservations")
    @GetMapping("/list")
    public ResponseEntity<ResponseListPageableReservation> listReservationPageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                                     @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                                     @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                                     @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(reservationService.listReservationByPageIn(pageNo, pageSize, sortBy, sortDir));
    }

    @Operation(summary = "List reservation by id")
    @GetMapping(value = "/{reservationId}")
    public ResponseEntity<ResponseReservationDetail> listReservationById(@PathVariable(name = "reservationId") Long reservationId){
        return ResponseEntity.ok(reservationService.findReservationByIdIn(reservationId));
    }

    @Operation(summary = "Save reservation")
    @PostMapping()
    public ResponseEntity<ReservationDTO> saveReservation(@RequestBody List<RequestReservation> requestReservationList){
        return ResponseEntity.ok(reservationService.createReservationIn(requestReservationList));
    }

    @Operation(summary = "Cancel reservation")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable(name = "reservationId") Long reservationId){
        return ResponseEntity.ok(reservationService.deleteReservationIn(reservationId));
    }
}
