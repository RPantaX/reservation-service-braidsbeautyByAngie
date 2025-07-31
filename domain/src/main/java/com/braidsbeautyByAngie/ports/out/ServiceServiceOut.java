package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.request.RequestServiceFilter;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;

import java.util.Optional;

public interface ServiceServiceOut {
    ServiceDTO createServiceOut(RequestService requestService);
    Optional<ResponseService> findServiceByIdOut(Long serviceId);
    ServiceDTO updateServiceOut(Long serviceId, RequestService requestService);
    ServiceDTO deleteServiceOut(Long serviceId);
    ResponseListPageableService listServiceByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir);
    ResponseListPageableService listServiceByPageByCategoryOut(int pageNumber, int pageSize, String orderBy, String sortDir, Long categoryId);
    ResponseListPageableService filterServicesOut(RequestServiceFilter filter);
}
