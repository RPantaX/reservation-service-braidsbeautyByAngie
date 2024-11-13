package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseListPageableWorkService;

public interface WorkServiceServiceOut {
    ResponseListPageableWorkService listWorkServiceByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir);
    WorkServiceDTO cancelWorkServiceOut(Long workServiceId);
}
