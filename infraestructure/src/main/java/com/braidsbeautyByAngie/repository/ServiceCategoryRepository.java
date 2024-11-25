package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.PromotionEntity;
import com.braidsbeautyByAngie.entity.ServiceCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategoryEntity, Long> {
    Boolean existsByServiceCategoryName(String categoryName);

    @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ServiceCategoryEntity c WHERE c.serviceCategoryId = :categoryId AND c.state = true")
    Boolean existByServiceCategoryIdAndStateTrue(Long categoryId);

    @Query(value = "SELECT c FROM ServiceCategoryEntity c WHERE c.serviceCategoryId = :categoryId AND c.state = true")
    Optional<ServiceCategoryEntity> findServiceCategoryIdAndStateTrue(Long categoryId);

    @Query("SELECT c FROM ServiceCategoryEntity c WHERE c.parentCategory IS NULL AND c.state=true")
    Page<ServiceCategoryEntity> findAllCategoriesPageableAndStatusTrue(Pageable pageable);


}
