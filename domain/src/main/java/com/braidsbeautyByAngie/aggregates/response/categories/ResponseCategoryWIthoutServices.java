package com.braidsbeautyByAngie.aggregates.response.categories;

import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import lombok.*;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseCategoryWIthoutServices {
    private ServiceCategoryDTO serviceCategoryDTO;
    private List<ResponseSubCategory> responseSubCategoryList;
    private List<PromotionDTO> promotionDTOList;
}
