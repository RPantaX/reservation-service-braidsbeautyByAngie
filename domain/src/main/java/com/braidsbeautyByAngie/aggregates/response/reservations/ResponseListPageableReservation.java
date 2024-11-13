package com.braidsbeautyByAngie.aggregates.response.reservations;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseListPageableReservation {
    private List<ResponseReservation> responseReservationList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
