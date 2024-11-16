package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategoryWIthoutServices;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseSubCategory;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;

import com.braidsbeautyByAngie.entity.ServiceCategoryEntity;
import com.braidsbeautyByAngie.entity.ServiceEntity;

import com.braidsbeautyByAngie.mapper.PromotionMapper;
import com.braidsbeautyByAngie.mapper.ServiceCategoryMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;

import com.braidsbeautyByAngie.repository.ServiceCategoryRepository;
import com.braidsbeautyByAngie.repository.ServiceRepository;
import com.braidsbeautybyangie.coreservice.aggregates.Constants;

import com.braidsbeautyByAngie.ports.out.ServiceServiceOut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceServiceAdapter implements ServiceServiceOut {
    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final PromotionMapper promotionMapper;

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceAdapter.class);


    @Override
    public ServiceDTO createServiceOut(RequestService requestService) {
        logger.info("Creating service with name: {}", requestService.getServiceName());
        if(serviceExistsByName(requestService.getServiceName())) throw new RuntimeException("The name of the service already exists");
        ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.findServiceCategoryIdAndStateTrue(requestService.getServiceCategoryId()).orElseThrow();

        ServiceEntity serviceEntity = ServiceEntity.builder()
                .serviceDescription(requestService.getServiceDescription())
                .serviceName(requestService.getServiceName())
                .serviceDescription(requestService.getServiceDescription())
                .serviceImage(requestService.getServiceImage())
                .serviceCategoryEntity(serviceCategorySaved)
                .durationTimeAprox(requestService.getDurationTimeAprox())
                .createdAt(Constants.getTimestamp())
                .modifiedByUser("TEST-CREATED")
                .state(Constants.STATUS_ACTIVE)
                .build();
        ServiceEntity serviceCreated = serviceRepository.save(serviceEntity);

        logger.info("Service '{}' created successfully with ID: {}",serviceCreated.getServiceName(),serviceCreated.getServiceId());
        return serviceMapper.mapServiceEntityToDTO(serviceCreated);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseService> findServiceByIdOut(Long serviceId) {
        logger.info("Searching for service with ID: {}", serviceId);

        // Obtener la entidad del servicio y verificar si está presente
        Optional<ServiceEntity> optionalServiceEntity = getServiceEntity(serviceId);
        if (optionalServiceEntity.isEmpty()) {
            logger.warn("Service with ID {} not found", serviceId);
            return Optional.empty();
        }

        ServiceEntity serviceEntity = optionalServiceEntity.get();

        // Verificar si la categoría del servicio es nula
        ServiceCategoryEntity serviceCategoryEntity = serviceEntity.getServiceCategoryEntity();
        List<ResponseSubCategory> subCategoryList = new ArrayList<>();
        List<PromotionDTO> promotionDTOList = new ArrayList<>();
        ServiceCategoryDTO serviceCategoryDTO = null;

        if (serviceCategoryEntity != null) {
            // Si no es nula, mapear subcategorías y promociones
            subCategoryList = serviceCategoryEntity.getSubCategories() != null
                    ? serviceCategoryEntity.getSubCategories()
                    .stream()
                    .map(subCat -> {
                        ResponseSubCategory responseSubCategory = new ResponseSubCategory();
                        responseSubCategory.setServiceCategoryDTO(serviceCategoryMapper.mapServiceEntityToDTO(subCat));
                        return responseSubCategory;
                    }).toList()
                    : new ArrayList<>();

            promotionDTOList = serviceCategoryEntity.getPromotionEntities() != null
                    ? promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities())
                    : new ArrayList<>();

            serviceCategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryEntity);
        }

        // Construir ResponseCategoryWIthoutServices con los valores obtenidos
        ResponseCategoryWIthoutServices categoryWIthoutServices = ResponseCategoryWIthoutServices
                .builder()
                .responseSubCategoryList(subCategoryList)
                .serviceCategoryDTO(serviceCategoryDTO)
                .promotionDTOList(promotionDTOList)
                .build();

        // Crear ResponseService y devolverlo
        ResponseService responseService = ResponseService.builder()
                .serviceDTO(serviceMapper.mapServiceEntityToDTO(serviceEntity))
                .responseCategoryWIthoutServices(categoryWIthoutServices)
                .build();

        logger.info("Service with ID {} found", serviceId);
        return Optional.of(responseService);
    }

    @Override
    public ServiceDTO updateServiceOut(Long serviceId, RequestService requestService) {
        logger.info("Searching for update service with ID: {}", serviceId);
        ServiceEntity serviceEntity = getServiceEntity(serviceId).get();
        serviceEntity.setServiceName(requestService.getServiceName());
        serviceEntity.setServiceDescription(requestService.getServiceDescription());
        serviceEntity.setServiceImage(requestService.getServiceImage());
        serviceEntity.setDurationTimeAprox(requestService.getDurationTimeAprox());
        serviceEntity.setModifiedByUser("TEST-UPDATED");
        serviceEntity.setModifiedAt(Constants.getTimestamp());

        //check if the category exists
        if(requestService.getServiceCategoryId() != null && serviceCategoryRepository.existByServiceCategoryIdAndStateTrue(requestService.getServiceCategoryId())){
                ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.findServiceCategoryIdAndStateTrue(requestService.getServiceCategoryId()).orElseThrow();
                serviceEntity.setServiceCategoryEntity(serviceCategorySaved);
        }
        else serviceEntity.setServiceCategoryEntity(null);

        ServiceEntity serviceUpdated = serviceRepository.save(serviceEntity);
        logger.info("Service updated with ID: {}", serviceUpdated.getServiceId());
        return serviceMapper.mapServiceEntityToDTO(serviceUpdated);
    }

    @Override
    public ServiceDTO deleteServiceOut(Long serviceId) {
        logger.info("Searching service for delete with ID: {}", serviceId);

        ServiceEntity serviceEntitySaved = getServiceEntity(serviceId).orElseThrow();
        serviceEntitySaved.setModifiedByUser("TEST-DELETED");
        serviceEntitySaved.setDeletedAt(Constants.getTimestamp());
        serviceEntitySaved.setState(Constants.STATUS_INACTIVE);
        serviceEntitySaved.setServiceCategoryEntity(null);

        ServiceEntity serviceDeleted = serviceRepository.save(serviceEntitySaved);
        logger.info("service deleted with ID: {}", serviceDeleted.getServiceId());

        return serviceMapper.mapServiceEntityToDTO(serviceDeleted);
    }

    @Override
    public ResponseListPageableService listServiceByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        logger.info("Searching all services with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if (serviceRepository.findAllByStateTrueAndPageable(pageable).isEmpty()) return null;

        Page<ServiceEntity> serviceEntityPage = serviceRepository.findAllByStateTrueAndPageable(pageable);

        List<ResponseService> responseServiceList = serviceEntityPage.getContent().stream().map(serviceEntity -> {

            // Verificar que ServiceCategoryEntity no sea nulo
            ServiceCategoryEntity serviceCategoryEntity = serviceEntity.getServiceCategoryEntity();
            ResponseCategoryWIthoutServices categoryWIthoutServices = null;

            if (serviceCategoryEntity != null) {
                // Construir subcategorías y promociones para la categoría del producto, verificando subcategorías nulas
                List<ResponseSubCategory> subCategoryList = serviceCategoryEntity.getSubCategories() != null
                        ? serviceCategoryEntity.getSubCategories().stream()
                        .map(subCat -> {
                            ResponseSubCategory responseSubCategory = new ResponseSubCategory();
                            responseSubCategory.setServiceCategoryDTO(serviceCategoryMapper.mapServiceEntityToDTO(subCat));
                            return responseSubCategory;
                        }).toList()
                        : Collections.emptyList();

                // Verificar que PromotionEntities no sea nulo
                List<PromotionDTO> promotionDTOList = serviceCategoryEntity.getPromotionEntities() != null
                        ? promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities())
                        : Collections.emptyList();

                // Crear ServiceCategoryDTO solo si serviceCategoryEntity no es nulo
                ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryEntity);

                categoryWIthoutServices = ResponseCategoryWIthoutServices.builder()
                        .responseSubCategoryList(subCategoryList)
                        .serviceCategoryDTO(serviceCategoryDTO)
                        .promotionDTOList(promotionDTOList)
                        .build();
            }

            return ResponseService.builder()
                    .serviceDTO(serviceMapper.mapServiceEntityToDTO(serviceEntity))
                    .responseCategoryWIthoutServices(categoryWIthoutServices)
                    .build();
        }).toList();

        logger.info("Services found with a total elements: {}", serviceEntityPage.getTotalElements());
        return ResponseListPageableService.builder()
                .responseServiceList(responseServiceList)
                .pageNumber(serviceEntityPage.getNumber())
                .totalElements(serviceEntityPage.getTotalElements())
                .totalPages(serviceEntityPage.getTotalPages())
                .pageSize(serviceEntityPage.getSize())
                .end(serviceEntityPage.isLast())
                .build();
    }

    private boolean serviceExistsByName(String serviceName){
        return serviceRepository.existsByServiceName(serviceName);
    }
    private boolean serviceExistsById(Long serviceId){
        return serviceRepository.existsByServiceIdAndStateTrue(serviceId);
    }
    private Optional<ServiceEntity> getServiceEntity(Long serviceId){
        if (!serviceExistsById(serviceId)) throw new RuntimeException("The service does not exist.");
        return serviceRepository.findServiceByIdWithStateTrue(serviceId);
    }

}
