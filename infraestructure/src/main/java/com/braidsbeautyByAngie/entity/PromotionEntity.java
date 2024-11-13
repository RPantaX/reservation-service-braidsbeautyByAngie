package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "promotion")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_Id", nullable = false)
    private Long promotionId;

    @Column(name = "promotion_name", nullable = true)
    private String promotionName;

    @Column(name = "promotion_description", nullable = true)
    private String promotionDescription;

    @Column(name = "promotion_discount_rate", nullable = true)
    private Double promotionDiscountRate;

    @Column(name = "promotion_start_date", nullable = true)
    private Timestamp promotionStartDate;

    @Column(name = "promotion_end_date", nullable = true)
    private Timestamp promotionEndDate;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = ServiceCategoryEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "promotion_service_category",
            joinColumns = @JoinColumn(name = "promotion_Id", referencedColumnName = "promotion_Id"),
            inverseJoinColumns = @JoinColumn(name = "service_category_Id", referencedColumnName = "service_category_Id"))
    private List<ServiceCategoryEntity> productCategoryEntities;

    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}
