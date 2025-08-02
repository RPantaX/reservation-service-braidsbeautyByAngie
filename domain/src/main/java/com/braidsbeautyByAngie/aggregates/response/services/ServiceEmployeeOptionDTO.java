package com.braidsbeautyByAngie.aggregates.response.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceEmployeeOptionDTO {
    private Long id;
    private String name;
    private String specialty;
    private Integer yearsExperience;
    private Double rating;
    private Boolean selected;
    private String employeeImage;
}
