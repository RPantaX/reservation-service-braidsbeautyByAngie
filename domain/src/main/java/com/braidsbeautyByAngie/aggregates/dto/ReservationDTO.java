package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationDTO {
    private Long reservationId;
    private String reservationState;
    private Long shoppingCartItemId;
    private Long shopOrderId;
    private BigDecimal reservationTotalPrice;
}
