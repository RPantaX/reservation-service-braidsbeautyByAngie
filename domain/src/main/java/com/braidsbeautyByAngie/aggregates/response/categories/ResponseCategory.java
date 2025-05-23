package com.braidsbeautyByAngie.aggregates.response.categories;

import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import lombok.*;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseCategory {
    private Long productCategoryId;
    private String productCategoryName;
    private List<ResponseSubCategory> responseSubCategoryList;
    private List<PromotionDTO> promotionDTOList;
}
