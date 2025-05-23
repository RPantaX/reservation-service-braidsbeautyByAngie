package com.braidsbeautyByAngie.aggregates.response.promotions;

import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponsePromotion {
    private PromotionDTO promotionDTO;
    private List<ServiceCategoryDTO> categoryDTOList;
}
