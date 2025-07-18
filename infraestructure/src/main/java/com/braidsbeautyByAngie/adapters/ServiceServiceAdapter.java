package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.constants.ReservationErrorEnum;
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

import com.braidsbeautyByAngie.ports.out.ServiceServiceOut;

import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class ServiceServiceAdapter implements ServiceServiceOut {
    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final PromotionMapper promotionMapper;

    @Override
    public ServiceDTO createServiceOut(RequestService requestService) {
        log.info("Creating service with name: {}", requestService.getServiceName());
        serviceExistsByName(requestService.getServiceName());
        ServiceCategoryEntity serviceCategorySaved = serviceCategoryRepository.findServiceCategoryIdAndStateTrue(requestService.getServiceCategoryId()).orElseThrow();

        ServiceEntity serviceEntity = ServiceEntity.builder()
                .serviceDescription(requestService.getServiceDescription())
                .serviceName(requestService.getServiceName())
                .serviceDescription(requestService.getServiceDescription())
                .serviceImage(requestService.getServiceImage())
                .servicePrice(requestService.getServicePrice())
                .serviceCategoryEntity(serviceCategorySaved)
                .durationTimeAprox(requestService.getDurationTimeAprox())
                .createdAt(Constants.getTimestamp())
                .modifiedByUser("TEST-CREATED")
                .state(Constants.STATUS_ACTIVE)
                .build();
        ServiceEntity serviceCreated = serviceRepository.save(serviceEntity);

        log.info("Service '{}' created successfully with ID: {}",serviceCreated.getServiceName(),serviceCreated.getServiceId());
        return serviceMapper.mapServiceEntityToDTO(serviceCreated);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseService> findServiceByIdOut(Long serviceId) {
        log.info("Searching for service with ID: {}", serviceId);

        // Obtener la entidad del servicio y verificar si está presente
        Optional<ServiceEntity> optionalServiceEntity = getServiceEntity(serviceId);
        if (optionalServiceEntity.isEmpty()) {
            log.warn("Service with ID {} not found", serviceId);
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

        log.info("Service with ID {} found", serviceId);
        return Optional.of(responseService);
    }

    @Override
    public ServiceDTO updateServiceOut(Long serviceId, RequestService requestService) {
        log.info("Searching for update service with ID: {}", serviceId);
        ServiceEntity serviceEntity =  serviceRepository.findServiceByIdWithStateTrue(serviceId).orElse(null);
        ValidateUtil.requerido(serviceEntity, ReservationErrorEnum.SERVICE_NOT_FOUND_ERS00015, "Service not found with ID: " + serviceId);
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
        log.info("Service updated with ID: {}", serviceUpdated.getServiceId());
        return serviceMapper.mapServiceEntityToDTO(serviceUpdated);
    }

    @Override
    public ServiceDTO deleteServiceOut(Long serviceId) {
        log.info("Searching service for delete with ID: {}", serviceId);
        ServiceEntity serviceEntitySaved =  serviceRepository.findServiceByIdWithStateTrue(serviceId).orElse(null);
        ValidateUtil.requerido(serviceEntitySaved, ReservationErrorEnum.SERVICE_NOT_FOUND_ERS00015, "Service not found with ID: " + serviceId);
        serviceEntitySaved.setModifiedByUser("TEST-DELETED");
        serviceEntitySaved.setDeletedAt(Constants.getTimestamp());
        serviceEntitySaved.setState(Constants.STATUS_INACTIVE);
        serviceEntitySaved.setServiceCategoryEntity(null);

        ServiceEntity serviceDeleted = serviceRepository.save(serviceEntitySaved);
        log.info("service deleted with ID: {}", serviceDeleted.getServiceId());

        return serviceMapper.mapServiceEntityToDTO(serviceDeleted);
    }

    @Override
    public ResponseListPageableService listServiceByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Searching all services with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

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

        log.info("Services found with a total elements: {}", serviceEntityPage.getTotalElements());
        return ResponseListPageableService.builder()
                .responseServiceList(responseServiceList)
                .pageNumber(serviceEntityPage.getNumber())
                .totalElements(serviceEntityPage.getTotalElements())
                .totalPages(serviceEntityPage.getTotalPages())
                .pageSize(serviceEntityPage.getSize())
                .end(serviceEntityPage.isLast())
                .build();
    }

    @Override
    public ResponseListPageableService listServiceByPageByCategoryOut(int pageNumber, int pageSize, String orderBy, String sortDir, Long categoryId) {
        log.info("Searching services by category ID: {} with the following parameters: {}", categoryId, Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<ServiceEntity> serviceEntityPage = serviceRepository.findAllByCategoryIdAndStateTrue(categoryId, pageable);

        if (serviceEntityPage.isEmpty()) {
            log.info("No services found for category ID: {}", categoryId);
            return null;
        }

        List<ResponseService> responseServiceList = serviceEntityPage.getContent().stream().map(serviceEntity -> {
            ServiceCategoryEntity serviceCategoryEntity = serviceEntity.getServiceCategoryEntity();
            ResponseCategoryWIthoutServices categoryWIthoutServices = null;

            if (serviceCategoryEntity != null) {
                List<ResponseSubCategory> subCategoryList = serviceCategoryEntity.getSubCategories() != null
                        ? serviceCategoryEntity.getSubCategories().stream()
                        .map(subCat -> {
                            ResponseSubCategory responseSubCategory = new ResponseSubCategory();
                            responseSubCategory.setServiceCategoryDTO(serviceCategoryMapper.mapServiceEntityToDTO(subCat));
                            return responseSubCategory;
                        }).toList()
                        : Collections.emptyList();

                List<PromotionDTO> promotionDTOList = serviceCategoryEntity.getPromotionEntities() != null
                        ? promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities())
                        : Collections.emptyList();

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

        log.info("Services found for category ID: {} with total elements: {}", categoryId, serviceEntityPage.getTotalElements());
        return ResponseListPageableService.builder()
                .responseServiceList(responseServiceList)
                .pageNumber(serviceEntityPage.getNumber())
                .totalElements(serviceEntityPage.getTotalElements())
                .totalPages(serviceEntityPage.getTotalPages())
                .pageSize(serviceEntityPage.getSize())
                .end(serviceEntityPage.isLast())
                .build();
    }

    private void serviceExistsByName(String serviceName){
        boolean serviceExists = serviceRepository.existsByServiceName(serviceName);
        if (serviceExists){
            log.error("Service with name '{}' already exists", serviceName);
            ValidateUtil.evaluar(!serviceExists, ReservationErrorEnum.SERVICE_ALREADY_EXISTS_ERS00016);
        }
    }
    private boolean serviceExistsById(Long serviceId){
        return serviceRepository.existsByServiceIdAndStateTrue(serviceId);
    }
    private Optional<ServiceEntity> getServiceEntity(Long serviceId){
        ServiceEntity serviceEntity = serviceRepository.findServiceByIdWithStateTrue(serviceId)
                .orElse(null);
        if (serviceEntity == null) {
            return Optional.empty();
        }
        return Optional.of(serviceEntity);
    }

}
