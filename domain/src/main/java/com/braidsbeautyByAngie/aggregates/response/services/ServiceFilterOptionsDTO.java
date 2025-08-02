package com.braidsbeautyByAngie.aggregates.response.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceFilterOptionsDTO {
    private List<ServiceCategoryOptionDTO> categories;
    private ServicePriceRangeDTO priceRange;
    private ServiceDurationRangeDTO durationRange;
    private List<ServiceEmployeeOptionDTO> employees;
}
