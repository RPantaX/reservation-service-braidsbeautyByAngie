package com.braidsbeautyByAngie.aggregates.response.workService;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseListPageableWorkService {
    private List<ResponseWorkService> responseWorkServiceList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
