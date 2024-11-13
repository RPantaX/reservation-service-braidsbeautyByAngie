package com.braidsbeautyByAngie.aggregates.response.services;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategoryWIthoutServices;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseService {
    private ServiceDTO serviceDTO;
    private ResponseCategoryWIthoutServices responseCategoryWIthoutServices;
}
