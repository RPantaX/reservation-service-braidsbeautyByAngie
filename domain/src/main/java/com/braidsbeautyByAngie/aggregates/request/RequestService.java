package com.braidsbeautyByAngie.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RequestService {
    private String serviceName;
    private String serviceDescription;
    private String serviceImage;
    private LocalTime durationTimeAprox;
    private Long serviceCategoryId;
    private BigDecimal servicePrice;
}
