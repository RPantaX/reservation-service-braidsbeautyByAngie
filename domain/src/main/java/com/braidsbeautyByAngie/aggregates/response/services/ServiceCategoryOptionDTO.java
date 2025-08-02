package com.braidsbeautyByAngie.aggregates.response.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceCategoryOptionDTO {
    private Long id;
    private String name;
    private Integer serviceCount;
    private Long parentId;
    private Boolean selected;
}