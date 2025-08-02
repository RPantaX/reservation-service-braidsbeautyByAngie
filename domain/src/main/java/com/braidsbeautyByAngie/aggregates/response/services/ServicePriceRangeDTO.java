package com.braidsbeautyByAngie.aggregates.response.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePriceRangeDTO {
    private BigDecimal min;
    private BigDecimal max;
}