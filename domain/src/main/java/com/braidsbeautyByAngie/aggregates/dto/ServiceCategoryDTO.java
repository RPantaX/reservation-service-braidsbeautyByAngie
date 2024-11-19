package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ServiceCategoryDTO {
    private Long serviceCategoryId;
    private String serviceCategoryName;
}
