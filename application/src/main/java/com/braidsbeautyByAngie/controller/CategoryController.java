package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestCategory;
import com.braidsbeautyByAngie.aggregates.request.RequestSubCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseListPageableCategory;
import com.braidsbeautyByAngie.ports.in.CategoryServiceIn;


import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@OpenAPIDefinition(
        info = @Info(
                title = "API-CATEGORY",
                version = "1.0",
                description = "Category management"
        )
)
@RestController
@RequestMapping("/v1/reservation-service/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceIn categoryService;

    @GetMapping("/list/pageable")
    public ResponseEntity<ApiResponse> listCategoryPageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(ApiResponse.ok("List of categories retrieved successfully",categoryService.listCategoryPageableIn(pageNo, pageSize, sortBy, sortDir)));
    }

    @GetMapping(value = "/{categoryId}")
    public ResponseEntity<ApiResponse> listCategoryById(@PathVariable(name = "categoryId") Long categoryId){
        return ResponseEntity.ok(ApiResponse.ok("LIST CATEGORY BY ID",categoryService.findCategoryByIdIn(categoryId)));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> saveCategory(@RequestBody RequestCategory requestCategory){
        return new ResponseEntity<>(ApiResponse.create("Category saved",categoryService.createCategoryIn(requestCategory)), HttpStatus.CREATED);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable(name = "categoryId") Long categoryId,@RequestBody RequestCategory requestCategory){
        return new ResponseEntity<>(ApiResponse.create("category saved", categoryService.updateCategoryIn(requestCategory,categoryId)), HttpStatus.CREATED);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable(name = "categoryId") Long categoryId){
        return ResponseEntity.ok(ApiResponse.ok("Category deleted",categoryService.deleteCategoryIn(categoryId)));
    }
    //subcategories
    @PostMapping("/subcategory")
    public ResponseEntity<ApiResponse> saveSubCategory(@RequestBody RequestSubCategory requestSubCategory){
        return new ResponseEntity<>(ApiResponse.create("SubCategory saved", categoryService.createSubCategoryIn(requestSubCategory)), HttpStatus.CREATED);
    }

    @PutMapping("/subcategory/{categoryId}")
    public ResponseEntity<ApiResponse> updateSubCategory(@PathVariable(name = "categoryId") Long categoryId,@RequestBody RequestSubCategory requestSubCategory){
        return ResponseEntity.ok(ApiResponse.create("sub categpry updated",categoryService.updateSubCategoryIn(requestSubCategory,categoryId)));
    }
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listCategory(){
        return ResponseEntity.ok(ApiResponse.ok("List of categories retrieved successfully",
                categoryService.listCategoryIn()));
    }
}
