package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestCategory;
import com.braidsbeautyByAngie.aggregates.request.RequestSubCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseListPageableCategory;
import com.braidsbeautyByAngie.ports.in.CategoryServiceIn;
import com.braidsbeautyByAngie.ports.out.CategoryServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryServiceIn {
    private final CategoryServiceOut categoryServiceOut;
    @Override
    public ServiceCategoryDTO createCategoryIn(RequestCategory requestCategory) {
        return categoryServiceOut.createCategoryOut(requestCategory);
    }

    @Override
    public ServiceCategoryDTO createSubCategoryIn(RequestSubCategory requestSubCategory) {
        return categoryServiceOut.createSubCategoryOut(requestSubCategory);
    }

    @Override
    public Optional<ResponseCategory> findCategoryByIdIn(Long categoryId) {
        return categoryServiceOut.findCategoryByIdOut(categoryId);
    }

    @Override
    public ServiceCategoryDTO updateCategoryIn(RequestCategory requestCategory, Long categoryId) {
        return categoryServiceOut.updateCategoryOut(requestCategory, categoryId);
    }

    @Override
    public ServiceCategoryDTO updateSubCategoryIn(RequestSubCategory requestSubCategory, Long categoryId) {
        return categoryServiceOut.updateSubCategoryOut(requestSubCategory, categoryId);
    }

    @Override
    public ServiceCategoryDTO deleteCategoryIn(Long categoryId) {
        return categoryServiceOut.deleteCategoryOut(categoryId);
    }

    @Override
    public ResponseListPageableCategory listCategoryPageableIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return categoryServiceOut.listCategoryPageableOut(pageNumber, pageSize, orderBy, sortDir);
    }
}
