package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestCategory;
import com.braidsbeautyByAngie.aggregates.request.RequestSubCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseListPageableCategory;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseSubCategory;
import com.braidsbeautyByAngie.entity.PromotionEntity;
import com.braidsbeautyByAngie.entity.ServiceCategoryEntity;
import com.braidsbeautyByAngie.mapper.PromotionMapper;
import com.braidsbeautyByAngie.mapper.ServiceCategoryMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;
import com.braidsbeautyByAngie.ports.out.CategoryServiceOut;
import com.braidsbeautyByAngie.repository.PromotionRepository;
import com.braidsbeautyByAngie.repository.ServiceCategoryRepository;


import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.GlobalErrorEnum;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceAdapter implements CategoryServiceOut {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final PromotionRepository promotionRepository;

    private final ServiceCategoryMapper serviceCategoryMapper;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional
    public ServiceCategoryDTO createCategoryOut(RequestCategory requestCategory) {

        log.info("Creating category with name: {}", requestCategory.getCategoryName());
        validateCategoryName(requestCategory.getCategoryName());

        ServiceCategoryEntity serviceCategoryEntity = ServiceCategoryEntity.builder()
                .serviceCategoryName(requestCategory.getCategoryName())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser("Test")
                .createdAt(Constants.getTimestamp())
                .build();
        if(!requestCategory.getPromotionListId().isEmpty()){
            List<PromotionEntity> promotionEntityList = promotionRepository.findAllByPromotionIdAndStateTrue(requestCategory.getPromotionListId());
            serviceCategoryEntity.setPromotionEntities(promotionEntityList);
        }

        ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.save(serviceCategoryEntity);
        log.info("Category '{}' created successfully with ID: {}",serviceCategorySaved.getServiceCategoryName(),serviceCategorySaved.getServiceCategoryId());
        return serviceCategoryMapper.mapServiceEntityToDTO(serviceCategorySaved);
    }

    @Override
    public ServiceCategoryDTO createSubCategoryOut(RequestSubCategory requestSubCategory) {
        log.info("Creating subcategory with name: {}", requestSubCategory.getServiceSubCategoryName());
        validateCategoryName(requestSubCategory.getServiceSubCategoryName());

        Optional<ServiceCategoryEntity> productCategoryParent = serviceCategoryRepository.findById(requestSubCategory.getServiceCategoryParentId());
        ServiceCategoryEntity serviceCategoryEntity = ServiceCategoryEntity.builder()
                .parentCategory(productCategoryParent.get())
                .serviceCategoryName(requestSubCategory.getServiceSubCategoryName())
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser("prueba")
                .build();
        ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.save(serviceCategoryEntity);
        log.info("SubCategory '{}' created successfully with ID: {}",serviceCategorySaved.getServiceCategoryName(),serviceCategorySaved.getServiceCategoryId());
        return serviceCategoryMapper.mapServiceEntityToDTO(serviceCategorySaved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseCategory> findCategoryByIdOut(Long categoryId) {
        log.info("Searching for Category with ID: {}", categoryId);
        ServiceCategoryEntity serviceCategoryEntity = getServiceCategoryEntity(categoryId);

        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryEntity);
        List<PromotionDTO> promotionDTOList = promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities());

        // Mapear subcategorías
        List<ResponseSubCategory> responseSubCategoryList = serviceCategoryEntity.getSubCategories()
                .stream()
                .map(subCategoryEntity -> {
                    ServiceCategoryDTO subcategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(subCategoryEntity);
                    return ResponseSubCategory.builder()
                            .serviceCategoryDTO(subcategoryDTO)
                            .build();
                })
                .collect(Collectors.toList());

        ResponseCategory responseCategory = ResponseCategory.builder()
                .productCategoryId(serviceCategoryDTO.getCategoryId())
                .productCategoryName(serviceCategoryDTO.getCategoryName())
                .responseSubCategoryList(responseSubCategoryList)
                .promotionDTOList(promotionDTOList)
                .build();
        log.info("Category with ID {} found", categoryId);
        return Optional.ofNullable(responseCategory);
    }

    @Override
    @Transactional
    public ServiceCategoryDTO updateCategoryOut(RequestCategory requestCategory, Long categoryId) {
        log.info("Searching for update category with ID: {}", categoryId);
        ServiceCategoryEntity serviceCategorySaved = getServiceCategoryEntity(categoryId);
        List<PromotionEntity> promotionEntitySet = promotionRepository.findAllByPromotionIdAndStateTrue(requestCategory.getPromotionListId());
        serviceCategorySaved.setServiceCategoryName(requestCategory.getCategoryName());
        serviceCategorySaved.setPromotionEntities(promotionEntitySet);

        ServiceCategoryEntity serviceCategoryUpdated = serviceCategoryRepository.save(serviceCategorySaved);
        log.info("Category updated with ID: {}",serviceCategoryUpdated.getServiceCategoryId());
        return serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryUpdated);
    }

    @Override
    public ServiceCategoryDTO updateSubCategoryOut(RequestSubCategory requestSubCategory, Long categoryId) {
        log.info("Searching for update subcategory with ID: {}", categoryId);
        ServiceCategoryEntity productSubCategory = getServiceCategoryEntity(categoryId);
        productSubCategory.setServiceCategoryName(requestSubCategory.getServiceSubCategoryName());

        ServiceCategoryEntity servicetCategoryUpdated = serviceCategoryRepository.save(productSubCategory);
        log.info("subcategory updated with ID: {}", servicetCategoryUpdated.getServiceCategoryId());

        return serviceCategoryMapper.mapServiceEntityToDTO(servicetCategoryUpdated);
    }

    @Override
    public ServiceCategoryDTO deleteCategoryOut(Long categoryId) {
        log.info("Searching category for delete with ID: {}", categoryId);
        ServiceCategoryEntity servicetCategorySaved = getServiceCategoryEntity(categoryId);
        servicetCategorySaved.setState(Constants.STATUS_INACTIVE);
        servicetCategorySaved.setModifiedByUser("PRUEBA");
        servicetCategorySaved.setDeletedAt(Constants.getTimestamp());

        ServiceCategoryEntity productCategoryDeleted = serviceCategoryRepository.save(servicetCategorySaved);
        log.info("Category deleted with ID: {}", categoryId);
        return serviceCategoryMapper.mapServiceEntityToDTO(productCategoryDeleted);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseListPageableCategory listCategoryPageableOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Searching all categories with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

        if (serviceCategoryRepository.findAll().isEmpty()) return null;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<ServiceCategoryEntity> page = serviceCategoryRepository.findAllCategoriesPageableAndStatusTrue(pageable);

        // Mapear cada ProductCategoryEntity a un ResponseCategory, incluyendo ProductDTO y PromotionDTO
        List<ResponseCategory> responseCategoryList = page.getContent().stream()
                .map(serviceCategoryEntity -> {
                    // Convertir la entidad de categoría en un DTO
                    ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryEntity);

                    // Convertir las entidades de producto y promoción a DTOs
                    List<PromotionDTO> promotionDTOList = promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities());

                    // Mapear subcategorías a ResponseSubCategory
                    List<ResponseSubCategory> responseSubCategoryList = serviceCategoryEntity.getSubCategories().stream()
                            .map(subCategoryEntity -> ResponseSubCategory.builder()
                                    .serviceCategoryDTO(serviceCategoryMapper.mapServiceEntityToDTO(subCategoryEntity))
                                    .build())
                            .collect(Collectors.toList());

                    // Crear y retornar el ResponseCategory
                    return ResponseCategory.builder()
                            .productCategoryId(serviceCategoryDTO.getCategoryId())
                            .productCategoryName(serviceCategoryDTO.getCategoryName())
                            .responseSubCategoryList(responseSubCategoryList)
                            .promotionDTOList(promotionDTOList)
                            .build();
                })
                .collect(Collectors.toList());
        log.info("Categories found with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
        // Crear y retornar el objeto paginado de respuesta
        return ResponseListPageableCategory.builder()
                .responseCategoryList(responseCategoryList)
                .pageNumber(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .end(page.isLast())
                .build();
    }

    @Override
    public List<ServiceCategoryDTO> listCategoryOut() {
        log.info("Searching all categories");
        return serviceCategoryRepository.findAllByStateTrue()
                .stream()
                .map(serviceCategoryMapper::mapServiceEntityToDTO)
                .collect(Collectors.toList());
    }

    private boolean categoryNameExistsByName(String categoryName){
        return serviceCategoryRepository.existsByServiceCategoryName(categoryName);
    }

    private boolean serviceCategoryExistsById(Long id){
        return serviceCategoryRepository.existByServiceCategoryIdAndStateTrue(id);
    }

    private ServiceCategoryEntity getServiceCategoryEntity(Long categoryId){
        ServiceCategoryEntity category = serviceCategoryRepository.findServiceCategoryIdAndStateTrue(categoryId).orElse(null);
        if(category == null) {
            log.error("Category with ID {} not found", categoryId);
            ValidateUtil.requerido(category, GlobalErrorEnum.CATEGORY_NOT_FOUND_ERC00008);
        }
        return category;
    }
    private void validateCategoryName(String categoryName) {
        boolean categoryExists = serviceCategoryRepository.existsByServiceCategoryName(categoryName);
        if(categoryExists){
            log.error("Category name '{}' already exists", categoryName);
            ValidateUtil.evaluar(!categoryExists, GlobalErrorEnum.CATEGORY_ALREADY_EXISTS_ERC00009);
        }

    }
}
