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

import com.braidsbeautybyangie.coreservice.aggregates.Constants;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class CategoryServiceAdapter implements CategoryServiceOut {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final PromotionRepository promotionRepository;

    private final ServiceCategoryMapper serviceCategoryMapper;
    private final PromotionMapper promotionMapper;
    private final ServiceMapper serviceMapper;
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceAdapter.class);

    @Override
    @Transactional
    public ServiceCategoryDTO createCategoryOut(RequestCategory requestCategory) {

        logger.info("Creating category with name: {}", requestCategory.getServiceCategoryName());
        if(categoryNameExistsByName(requestCategory.getServiceCategoryName())) throw new RuntimeException("The category name already exists.");

        ServiceCategoryEntity serviceCategoryEntity = ServiceCategoryEntity.builder()
                .serviceCategoryName(requestCategory.getServiceCategoryName())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser("Test")
                .createdAt(Constants.getTimestamp())
                .build();
        if(!requestCategory.getPromotionListId().isEmpty()){
            List<PromotionEntity> promotionEntityList = promotionRepository.findAllByPromotionIdAndStateTrue(requestCategory.getPromotionListId());
            serviceCategoryEntity.setPromotionEntities(promotionEntityList);
        }

        ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.save(serviceCategoryEntity);
        logger.info("Category '{}' created successfully with ID: {}",serviceCategorySaved.getServiceCategoryName(),serviceCategorySaved.getServiceCategoryId());
        return serviceCategoryMapper.mapServiceEntityToDTO(serviceCategorySaved);
    }

    @Override
    public ServiceCategoryDTO createSubCategoryOut(RequestSubCategory requestSubCategory) {
        logger.info("Creating subcategory with name: {}", requestSubCategory.getServiceSubCategoryName());
        if (categoryNameExistsByName(requestSubCategory.getServiceSubCategoryName()) ) throw new RuntimeException("The name of the category already exists");
        Optional<ServiceCategoryEntity> productCategoryParent = serviceCategoryRepository.findById(requestSubCategory.getServiceCategoryParentId());
        ServiceCategoryEntity serviceCategoryEntity = ServiceCategoryEntity.builder()
                .parentCategory(productCategoryParent.get())
                .serviceCategoryName(requestSubCategory.getServiceSubCategoryName())
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser("prueba")
                .build();
        ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.save(serviceCategoryEntity);
        logger.info("SubCategory '{}' created successfully with ID: {}",serviceCategorySaved.getServiceCategoryName(),serviceCategorySaved.getServiceCategoryId());
        return serviceCategoryMapper.mapServiceEntityToDTO(serviceCategorySaved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseCategory> findCategoryByIdOut(Long categoryId) {
        logger.info("Searching for Category with ID: {}", categoryId);
        Optional<ServiceCategoryEntity> serviceCategoryEntity = getServiceCategoryEntity(categoryId);

        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryEntity.get());
        List<ServiceDTO> productDTOList = serviceCategoryEntity.get().getServiceEntities().stream().map(serviceMapper::mapServiceEntityToDTO).collect(Collectors.toList());
        List<PromotionDTO> promotionDTOList = promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.get().getPromotionEntities());

        // Mapear subcategorías
        List<ResponseSubCategory> responseSubCategoryList = serviceCategoryEntity.get().getSubCategories()
                .stream()
                .map(subCategoryEntity -> {
                    ServiceCategoryDTO subcategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(subCategoryEntity);
                    return ResponseSubCategory.builder()
                            .serviceCategoryDTO(subcategoryDTO)
                            .build();
                })
                .collect(Collectors.toList());

        ResponseCategory responseCategory = ResponseCategory.builder()
                .serviceCategoryDTO(serviceCategoryDTO)
                .responseSubCategoryList(responseSubCategoryList)
                .serviceDTOList(productDTOList)
                .promotionDTOList(promotionDTOList)
                .build();
        logger.info("Category with ID {} found", categoryId);
        return Optional.ofNullable(responseCategory);
    }

    @Override
    @Transactional
    public ServiceCategoryDTO updateCategoryOut(RequestCategory requestCategory, Long categoryId) {
        logger.info("Searching for update category with ID: {}", categoryId);
        Optional<ServiceCategoryEntity> serviceCategorySaved = getServiceCategoryEntity(categoryId);
        List<PromotionEntity> promotionEntitySet = promotionRepository.findAllByPromotionIdAndStateTrue(requestCategory.getPromotionListId());
        serviceCategorySaved.get().setServiceCategoryName(requestCategory.getServiceCategoryName());
        serviceCategorySaved.get().setPromotionEntities(promotionEntitySet);

        ServiceCategoryEntity serviceCategoryUpdated = serviceCategoryRepository.save(serviceCategorySaved.get());
        logger.info("Category updated with ID: {}",serviceCategoryUpdated.getServiceCategoryId());
        return serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryUpdated);
    }

    @Override
    public ServiceCategoryDTO updateSubCategoryOut(RequestSubCategory requestSubCategory, Long categoryId) {
        logger.info("Searching for update subcategory with ID: {}", categoryId);
        Optional<ServiceCategoryEntity> productSubCategory = getServiceCategoryEntity(categoryId);
        productSubCategory.get().setServiceCategoryName(requestSubCategory.getServiceSubCategoryName());

        ServiceCategoryEntity servicetCategoryUpdated = serviceCategoryRepository.save(productSubCategory.get());
        logger.info("subcategory updated with ID: {}", servicetCategoryUpdated.getServiceCategoryId());

        return serviceCategoryMapper.mapServiceEntityToDTO(servicetCategoryUpdated);
    }

    @Override
    public ServiceCategoryDTO deleteCategoryOut(Long categoryId) {
        logger.info("Searching category for delete with ID: {}", categoryId);
        Optional<ServiceCategoryEntity> servicetCategorySaved = getServiceCategoryEntity(categoryId);
        servicetCategorySaved.get().setState(Constants.STATUS_INACTIVE);
        servicetCategorySaved.get().setModifiedByUser("PRUEBA");
        servicetCategorySaved.get().setDeletedAt(Constants.getTimestamp());

        ServiceCategoryEntity productCategoryDeleted = serviceCategoryRepository.save(servicetCategorySaved.get());
        logger.info("Category deleted with ID: {}", categoryId);
        return serviceCategoryMapper.mapServiceEntityToDTO(productCategoryDeleted);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseListPageableCategory listCategoryPageableOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        logger.info("Searching all categories with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

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
                    List<ServiceDTO> serviceDTOList = serviceCategoryEntity.getServiceEntities().stream().map(serviceMapper::mapServiceEntityToDTO).collect(Collectors.toList());
                    List<PromotionDTO> promotionDTOList = promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities());

                    // Mapear subcategorías a ResponseSubCategory
                    List<ResponseSubCategory> responseSubCategoryList = serviceCategoryEntity.getSubCategories().stream()
                            .map(subCategoryEntity -> ResponseSubCategory.builder()
                                    .serviceCategoryDTO(serviceCategoryMapper.mapServiceEntityToDTO(subCategoryEntity))
                                    .build())
                            .collect(Collectors.toList());

                    // Crear y retornar el ResponseCategory
                    return ResponseCategory.builder()
                            .serviceCategoryDTO(serviceCategoryDTO)
                            .serviceDTOList(serviceDTOList)
                            .responseSubCategoryList(responseSubCategoryList)
                            .promotionDTOList(promotionDTOList)
                            .build();
                })
                .collect(Collectors.toList());
        logger.info("Categories found with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
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

    private boolean categoryNameExistsByName(String categoryName){
        return serviceCategoryRepository.existsByServiceCategoryName(categoryName);
    }

    private boolean serviceCategoryExistsById(Long id){
        return serviceCategoryRepository.existByServiceCategoryIdAndStateTrue(id);
    }

    private Optional<ServiceCategoryEntity> getServiceCategoryEntity(Long categoryId){
        if ( !serviceCategoryExistsById(categoryId) ) throw new RuntimeException("The category or subcategory does not exist.");
        return  serviceCategoryRepository.findServiceCategoryIdAndStateTrue(categoryId);
    }
}
