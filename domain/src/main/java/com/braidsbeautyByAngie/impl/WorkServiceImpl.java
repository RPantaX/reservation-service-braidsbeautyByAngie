package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseListPageableWorkService;
import com.braidsbeautyByAngie.ports.in.WorkServiceServiceIn;
import com.braidsbeautyByAngie.ports.out.WorkServiceServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkServiceServiceIn {

    private final WorkServiceServiceOut workServiceServiceOut;

    @Override
    public ResponseListPageableWorkService listWorkServiceByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return workServiceServiceOut.listWorkServiceByPageOut(pageNumber, pageSize, orderBy, sortDir);
    }

    @Override
    public WorkServiceDTO cancelWorkServiceIn(Long workServiceId) {
        return workServiceServiceOut.cancelWorkServiceOut(workServiceId);
    }
}
