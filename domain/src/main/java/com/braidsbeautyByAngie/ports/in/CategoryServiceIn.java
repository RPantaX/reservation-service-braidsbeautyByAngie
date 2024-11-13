package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestCategory;
import com.braidsbeautyByAngie.aggregates.request.RequestSubCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseListPageableCategory;

import java.util.Optional;

public interface CategoryServiceIn {
    ServiceCategoryDTO createCategoryIn(RequestCategory requestCategory);

    ServiceCategoryDTO createSubCategoryIn(RequestSubCategory requestSubCategory);

    Optional<ResponseCategory> findCategoryByIdIn(Long categoryId);

    ServiceCategoryDTO updateCategoryIn(RequestCategory requestCategory, Long categoryId);

    ServiceCategoryDTO updateSubCategoryIn(RequestSubCategory requestSubCategory, Long categoryId);

    ServiceCategoryDTO deleteCategoryIn(Long categoryId);

    ResponseListPageableCategory listCategoryPageableIn(int pageNumber, int pageSize, String orderBy, String sortDir);
}
