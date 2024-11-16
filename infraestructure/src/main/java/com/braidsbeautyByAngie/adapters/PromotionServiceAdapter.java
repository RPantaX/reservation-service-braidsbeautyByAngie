package com.braidsbeautyByAngie.adapters;


import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestPromotion;
import com.braidsbeautyByAngie.aggregates.response.promotions.ResponseListPageablePromotion;
import com.braidsbeautyByAngie.aggregates.response.promotions.ResponsePromotion;
import com.braidsbeautyByAngie.entity.PromotionEntity;
import com.braidsbeautyByAngie.entity.ServiceCategoryEntity;
import com.braidsbeautyByAngie.mapper.PromotionMapper;
import com.braidsbeautyByAngie.mapper.ServiceCategoryMapper;
import com.braidsbeautyByAngie.ports.out.PromotionServiceOut;
import com.braidsbeautyByAngie.repository.PromotionRepository;

import com.braidsbeautybyangie.coreservice.aggregates.Constants;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PromotionServiceAdapter implements PromotionServiceOut {
    private final PromotionRepository promotionRepository;

    private final PromotionMapper promotionMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;

    private static final Logger logger = LoggerFactory.getLogger(PromotionServiceAdapter.class);

    @Override
    public PromotionDTO createPromotionOut(RequestPromotion requestPromotion) {
        logger.info("Creating promotion with name: {}", requestPromotion.getPromotionName());
        if(promotionExistByName(requestPromotion.getPromotionName()))  throw new RuntimeException("The name of the promotion already exists");
        PromotionEntity promotionEntity = PromotionEntity.builder()
                .promotionName(requestPromotion.getPromotionName())
                .promotionDescription(requestPromotion.getPromotionDescription())
                .promotionDiscountRate(requestPromotion.getPromotionDiscountRate())
                .promotionStartDate(requestPromotion.getPromotionStartDate())
                .promotionEndDate(requestPromotion.getPromotionEndDate())
                .createdAt(Constants.getTimestamp())
                .modifiedByUser("TEST")
                .state(Constants.STATUS_ACTIVE)
                .build();
        PromotionEntity promotionSaved = promotionRepository.save(promotionEntity);
        logger.info("Promotion '{}' created successfully with ID: {}",promotionSaved.getPromotionName(),promotionSaved.getPromotionId());
        return promotionMapper.mapPromotionEntityToDto(promotionSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponsePromotion> findPromotionByIdOut(Long promotionId) {
        logger.info("Searching for promotion with ID: {}", promotionId);
        PromotionEntity promotionEntity = getPromotionEntity(promotionId).get();

        List<ServiceCategoryEntity> serviceCategoryEntityList = promotionEntity.getProductCategoryEntities().stream().toList();
        List<ServiceCategoryDTO> productCategoryDTOList = serviceCategoryEntityList.stream().map(serviceCategoryMapper::mapServiceEntityToDTO).toList();
        ResponsePromotion responsePromotion = ResponsePromotion.builder()
                .promotionDTO(promotionMapper.mapPromotionEntityToDto(promotionEntity))
                .serviceCategoryDTOList(productCategoryDTOList)
                .build();
        logger.info("Promotion with ID {} found", promotionId);
        return Optional.of(responsePromotion);
    }

    @Override
    public PromotionDTO updatePromotionOut(Long promotionId, RequestPromotion requestPromotion) {
        logger.info("Searching for update promotion with ID: {}", promotionId);
        PromotionEntity promotionEntity = getPromotionEntity(promotionId).get();
        promotionEntity.setPromotionName(requestPromotion.getPromotionName());
        promotionEntity.setPromotionDescription(requestPromotion.getPromotionDescription());
        promotionEntity.setPromotionDiscountRate(requestPromotion.getPromotionDiscountRate());
        promotionEntity.setPromotionStartDate(requestPromotion.getPromotionStartDate());
        promotionEntity.setPromotionEndDate(requestPromotion.getPromotionEndDate());
        promotionEntity.setModifiedByUser("TEST");
        promotionEntity.setModifiedAt(Constants.getTimestamp());

        PromotionEntity promotionEntityUpdated = promotionRepository.save(promotionEntity);
        logger.info("promotion updated with ID: {}", promotionEntityUpdated.getPromotionId());
        return promotionMapper.mapPromotionEntityToDto(promotionEntityUpdated);
    }

    @Override
    public PromotionDTO deletePromotionOut(Long promotionId) {
        logger.info("Searching promotion for delete with ID: {}", promotionId);
        List<ServiceCategoryEntity> serviceCategoryEntityList = new ArrayList<>();
        PromotionEntity promotionEntityOptional = getPromotionEntity(promotionId).get();
        promotionEntityOptional.setModifiedByUser("TEST");
        promotionEntityOptional.setDeletedAt(Constants.getTimestamp());
        promotionEntityOptional.setProductCategoryEntities(serviceCategoryEntityList);
        promotionEntityOptional.setState(Constants.STATUS_INACTIVE);
        PromotionEntity promotionDeleted = promotionRepository.save(promotionEntityOptional);
        logger.info("Product deleted with ID: {}", promotionId);
        return promotionMapper.mapPromotionEntityToDto(promotionDeleted);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseListPageablePromotion listPromotionByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        logger.info("Searching all promotions with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Usar la consulta personalizada con LEFT JOIN FETCH
        Page<PromotionEntity> promotionEntityPage = promotionRepository.findAllByStateTrueAndPageable(pageable);

        if (promotionEntityPage.isEmpty()) return null;

        List<ResponsePromotion> responsePromotionList = promotionEntityPage.getContent().stream().map(promotionEntity -> {
            // Mapear las categorías solo si existen, de lo contrario, devolver una lista vacía
            List<ServiceCategoryDTO> serviceCategoryDTOList = promotionEntity.getProductCategoryEntities() != null
                    ? promotionEntity.getProductCategoryEntities().stream()
                    .map(serviceCategoryMapper::mapServiceEntityToDTO)
                    .toList()
                    : List.of();

            return ResponsePromotion.builder()
                    .promotionDTO(promotionMapper.mapPromotionEntityToDto(promotionEntity))
                    .serviceCategoryDTOList(serviceCategoryDTOList)
                    .build();
        }).toList();

        logger.info("Promotions found with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

        return ResponseListPageablePromotion.builder()
                .responsePromotionList(responsePromotionList)
                .pageNumber(promotionEntityPage.getNumber())
                .totalElements(promotionEntityPage.getTotalElements())
                .totalPages(promotionEntityPage.getTotalPages())
                .pageSize(promotionEntityPage.getSize())
                .end(promotionEntityPage.isLast())
                .build();
    }

    private boolean promotionExistByName(String promotionName) {
        return promotionRepository.existsByPromotionName(promotionName);
    }
    private boolean promotionExistById(Long promotionId) {
        return promotionRepository.existsByPromotionIdAndStateTrue(promotionId);
    }
    private Optional<PromotionEntity> getPromotionEntity(Long promotionId) {
        if (!promotionExistById(promotionId)) throw new RuntimeException("The promotion does not exist.");
        return promotionRepository.findPromotionByIdWithStateTrue(promotionId);
    }
}
