package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.constants.ReservationErrorEnum;
import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.dto.rest.EmployeeDto;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.request.RequestServiceFilter;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategoryWIthoutServices;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseSubCategory;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;

import com.braidsbeautyByAngie.aggregates.response.services.ServiceEmployeeOptionDTO;
import com.braidsbeautyByAngie.aggregates.response.services.ServiceFilterOptionsDTO;
import com.braidsbeautyByAngie.entity.ServiceCategoryEntity;
import com.braidsbeautyByAngie.entity.ServiceEntity;

import com.braidsbeautyByAngie.mapper.PromotionMapper;
import com.braidsbeautyByAngie.mapper.ServiceCategoryMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;

import com.braidsbeautyByAngie.repository.ServiceCategoryRepository;
import com.braidsbeautyByAngie.repository.ServiceRepository;

import com.braidsbeautyByAngie.ports.out.ServiceServiceOut;

import com.braidsbeautyByAngie.rest.RestUsersAdapter;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceAdapter implements ServiceServiceOut {
    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final PromotionMapper promotionMapper;

    private final RestUsersAdapter restUsersAdapter;
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
                .modifiedByUser(com.braidsbeautyByAngie.aggregates.constants.Constants.getUserInSession())
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
        serviceEntity.setModifiedByUser(com.braidsbeautyByAngie.aggregates.constants.Constants.getUserInSession());
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
        serviceEntitySaved.setModifiedByUser(com.braidsbeautyByAngie.aggregates.constants.Constants.getUserInSession());
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

    @Override
    public ResponseListPageableService filterServicesOut(RequestServiceFilter filter) {
        log.info("Processing service filter request in application service");

        // Validaciones de negocio
        validateServiceFilterRequest(filter);

        return serviceRepository.filterServices(filter);
    }
    @Override
    public ServiceFilterOptionsDTO getServiceFilterOptionsOut() {
        log.info("Getting service filter options with employees");

        try {
            // 1. Obtener opciones básicas desde el repositorio (categorías, precios, duración)
            ServiceFilterOptionsDTO filterOptions = serviceRepository.getServiceFilterOptions();

            // 2. Obtener empleados desde el microservicio de usuarios
            List<ServiceEmployeeOptionDTO> employees = getEmployeeOptions();

            // 3. Establecer la lista de empleados en las opciones de filtrado
            filterOptions.setEmployees(employees);

            log.info("Service filter options retrieved successfully with {} employees", employees.size());
            return filterOptions;

        } catch (Exception e) {
            log.error("Error getting service filter options: {}", e.getMessage(), e);

            // En caso de error con el FeignClient, devolver al menos las opciones básicas
            ServiceFilterOptionsDTO basicOptions = serviceRepository.getServiceFilterOptions();
            basicOptions.setEmployees(new ArrayList<>());

            log.warn("Returning basic filter options without employees due to error");
            return basicOptions;
        }
    }
    /**
     * Obtiene los empleados disponibles para filtrado desde el microservicio de usuarios
     */
    private List<ServiceEmployeeOptionDTO> getEmployeeOptions() {
        try {
            log.info("Fetching employees from user microservice");

            // Llamar al microservicio de usuarios para obtener todos los empleados activos
            List<EmployeeDto> response = restUsersAdapter.getAllEmployees().getData();

            if (response != null) {
                // Convertir a EmployeeOption para el filtrado
                List<ServiceEmployeeOptionDTO> employeeOptions = response.stream()
                        .map(this::mapToEmployeeOption)
                        .filter(Objects::nonNull) // Filtrar nulos por si acaso
                        .collect(Collectors.toList());

                log.info("Successfully retrieved {} employees for filtering", employeeOptions.size());
                return employeeOptions;
            } else {
                log.warn("Empty or null response from employee service");
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("Error fetching employees from user microservice: {}", e.getMessage(), e);

            // En caso de error, devolver lista vacía para no romper el filtrado
            return new ArrayList<>();
        }
    }

    /**
     * Mapea EmployeeFilterResponse a EmployeeOption
     */
    private ServiceEmployeeOptionDTO mapToEmployeeOption(EmployeeDto employeeResponse) {
        if (employeeResponse == null) {
            return null;
        }

        try {
            // Construir el nombre del empleado
            String employeeName = employeeResponse.getPerson().getFullName();
            if (employeeName == null || employeeName.trim().isEmpty()) {
                // Fallback al nombre de la persona si el employeeName está vacío
                if (employeeResponse.getPerson() != null) {
                    employeeName = employeeResponse.getPerson().getFullName();
                }
            }

            // Obtener la especialidad del tipo de empleado
            String specialty = null;
            if (employeeResponse.getEmployeeType() != null) {
                specialty = employeeResponse.getEmployeeType().getValue();
            }

            return ServiceEmployeeOptionDTO.builder()
                    .id(employeeResponse.getId())
                    .name(employeeName != null ? employeeName : "Empleado sin nombre")
                    .specialty(specialty)
                    .employeeImage(employeeResponse.getEmployeeImage())
                    .yearsExperience(null) // Esto se podría calcular si tienes fecha de inicio
                    .rating(null) // Esto se podría obtener de un sistema de calificaciones
                    .selected(false)
                    .build();

        } catch (Exception e) {
            log.error("Error mapping employee response to option: {}", e.getMessage(), e);
            return null;
        }
    }
    private void validateServiceFilterRequest(RequestServiceFilter filter) {
        // Validar rangos de precio
        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            if (filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {
                throw new IllegalArgumentException("El precio mínimo no puede ser mayor al precio máximo");
            }
        }

        // Validar rangos de descuento
        if (filter.getMinDiscountRate() != null && filter.getMaxDiscountRate() != null) {
            if (filter.getMinDiscountRate().compareTo(filter.getMaxDiscountRate()) > 0) {
                throw new IllegalArgumentException("La tasa de descuento mínima no puede ser mayor a la máxima");
            }
        }

        // Validar duración en minutos
        if (filter.getMinDurationMinutes() != null && filter.getMaxDurationMinutes() != null) {
            if (filter.getMinDurationMinutes() > filter.getMaxDurationMinutes()) {
                throw new IllegalArgumentException("La duración mínima no puede ser mayor a la máxima");
            }
        }

        // Validar duración LocalTime
        if (filter.getMinDuration() != null && filter.getMaxDuration() != null) {
            if (filter.getMinDuration().isAfter(filter.getMaxDuration())) {
                throw new IllegalArgumentException("La duración mínima no puede ser mayor a la máxima");
            }
        }

        // Validar paginación
        if (filter.getPageNumber() < 0) {
            filter.setPageNumber(0);
        }

        if (filter.getPageSize() <= 0 || filter.getPageSize() > 100) {
            filter.setPageSize(10);
        }

        // Validar valores de duración en minutos (no pueden ser negativos)
        if (filter.getMinDurationMinutes() != null && filter.getMinDurationMinutes() < 0) {
            filter.setMinDurationMinutes(0);
        }

        if (filter.getMaxDurationMinutes() != null && filter.getMaxDurationMinutes() < 0) {
            filter.setMaxDurationMinutes(null);
        }
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
