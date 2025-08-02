package com.braidsbeautyByAngie.repository.dao;

import com.braidsbeautyByAngie.aggregates.request.RequestServiceFilter;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ServiceFilterOptionsDTO;

public interface ServiceRepositoryCustom {
    public ResponseListPageableService filterServices(RequestServiceFilter filter);
    ServiceFilterOptionsDTO getServiceFilterOptions();
}
