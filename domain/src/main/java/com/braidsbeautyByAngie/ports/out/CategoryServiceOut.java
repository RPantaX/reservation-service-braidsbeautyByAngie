package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestCategory;
import com.braidsbeautyByAngie.aggregates.request.RequestSubCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseListPageableCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryServiceOut {
    ServiceCategoryDTO createCategoryOut(RequestCategory requestCategory);

    ServiceCategoryDTO createSubCategoryOut(RequestSubCategory requestSubCategory);

    Optional<ResponseCategory> findCategoryByIdOut(Long categoryId);

    ServiceCategoryDTO updateCategoryOut(RequestCategory requestCategory, Long categoryId);

    ServiceCategoryDTO updateSubCategoryOut(RequestSubCategory requestSubCategory, Long categoryId);

    ServiceCategoryDTO deleteCategoryOut(Long categoryId);

    ResponseListPageableCategory listCategoryPageableOut(int pageNumber, int pageSize, String orderBy, String sortDir);
    List<ServiceCategoryDTO> listCategoryOut();
}
