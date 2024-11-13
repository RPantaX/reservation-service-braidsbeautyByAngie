package com.braidsbeautyByAngie.aggregates.response.schedules;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseListPageableSchedule {
    private List<ResponseSchedule> responseScheduleList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
