package com.braidsbeautyByAngie.aggregates.response.services;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDurationRangeDTO {
    private Integer minMinutes;
    private Integer maxMinutes;
}
