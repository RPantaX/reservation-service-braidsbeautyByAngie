package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseListPageableWorkService;

public interface WorkServiceServiceIn {
    ResponseListPageableWorkService listWorkServiceByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir);
    WorkServiceDTO cancelWorkServiceIn(Long workServiceId);
}
