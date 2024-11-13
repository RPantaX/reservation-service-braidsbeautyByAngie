package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ServiceDTO {
    private Long serviceId;
    private String serviceName;
    private String serviceDescription;
    private String serviceImage;
    private LocalTime durationTimeAprox;
}
