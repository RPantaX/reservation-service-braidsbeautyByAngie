package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationDTO {
    private Long reservationId;
    private String reservationState;
    private Long shoppingCartItemId;
    private Long orderLineId;
}
