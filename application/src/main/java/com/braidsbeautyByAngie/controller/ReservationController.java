package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservationDetail;
import com.braidsbeautyByAngie.auth.RequireRole;
import com.braidsbeautyByAngie.ports.in.ReservationServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse> listReservationPageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                   @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                   @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                   @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(ApiResponse.ok("List reservation pageable", reservationService.listReservationByPageIn(pageNo, pageSize, sortBy, sortDir)));
    }

    @Operation(summary = "List reservation by id")
    @GetMapping(value = "/{reservationId}")
    public ResponseEntity<ApiResponse> listReservationById(@PathVariable(name = "reservationId") Long reservationId){
        return ResponseEntity.ok(ApiResponse.ok("List reservation by id", reservationService.findReservationByIdIn(reservationId)));
    }

    @Operation(summary = "Save reservation")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @PostMapping()
    public ResponseEntity<ApiResponse> saveReservation(@RequestBody List<RequestReservation> requestReservationList){
        return new ResponseEntity<>(ApiResponse.create("Save reservation", reservationService.createReservationIn(requestReservationList)), HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel reservation")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable(name = "reservationId") Long reservationId){
        return ResponseEntity.ok(ApiResponse.ok("Cancel reservation", reservationService.deleteReservationIn(reservationId)));
    }
}
