package com.braidsbeautyByAngie.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "service_category")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_category_Id", nullable = false)
    private Long serviceCategoryId;

    @Column(name = "service_category_name", nullable = false)
    private String serviceCategoryName;

    // Relación de "padre" (ManyToOne) a "hijos" (OneToMany) en la misma entidad
    @ManyToOne
    @JoinColumn(name = "service_category_parent_Id")
    private ServiceCategoryEntity parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<ServiceCategoryEntity> subCategories = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = PromotionEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "Promotion_Product_Category",
            joinColumns = @JoinColumn(name = "service_category_Id", referencedColumnName = "service_category_Id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_Id", referencedColumnName = "promotion_Id"))
    private List<PromotionEntity> promotionEntities;

    @OneToMany(mappedBy = "serviceCategoryEntity", cascade = CascadeType.ALL) //Cascade en hibernate significa que cualquier operacion que le hagamos al producto también será para todos los objetos relacionados.
    private List<ServiceEntity> serviceEntities= new ArrayList<>();

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
