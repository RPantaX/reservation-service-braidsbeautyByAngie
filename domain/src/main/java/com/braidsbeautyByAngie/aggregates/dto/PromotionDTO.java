package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromotionDTO {
    private Long promotionId;

    private String promotionName;

    private String promotionDescription;

    private BigDecimal promotionDiscountRate;

    private Timestamp promotionStartDate;

    private Timestamp promotionEndDate;
}
