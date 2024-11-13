package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;
import com.braidsbeautyByAngie.ports.in.ServiceServiceIn;
import com.braidsbeautyByAngie.ports.out.ServiceServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceServiceIn {

    private final ServiceServiceOut serviceServiceOut;

    @Override
    public ServiceDTO createServiceIn(RequestService requestService) {
        return serviceServiceOut.createServiceOut(requestService);
    }

    @Override
    public Optional<ResponseService> findServiceByIdIn(Long serviceId) {
        return serviceServiceOut.findServiceByIdOut(serviceId);
    }

    @Override
    public ServiceDTO updateServiceIn(Long serviceId, RequestService requestService) {
        return serviceServiceOut.updateServiceOut(serviceId, requestService);
    }

    @Override
    public ServiceDTO deleteServiceIn(Long serviceId) {
        return serviceServiceOut.deleteServiceOut(serviceId);
    }

    @Override
    public ResponseListPageableService listServiceByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return serviceServiceOut.listServiceByPageOut(pageNumber, pageSize, orderBy, sortDir);
    }
}
