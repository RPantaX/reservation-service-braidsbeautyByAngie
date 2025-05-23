package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;

import java.util.Optional;

public interface ServiceServiceIn {
    ServiceDTO createServiceIn(RequestService requestService);
    Optional<ResponseService> findServiceByIdIn(Long serviceId);
    ServiceDTO updateServiceIn(Long serviceId, RequestService requestService);
    ServiceDTO deleteServiceIn(Long serviceId);
    ResponseListPageableService listServiceByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir);
    ResponseListPageableService listServiceByPageByCategoryIn(int pageNumber, int pageSize, String orderBy, String sortDir, Long categoryId);
}
