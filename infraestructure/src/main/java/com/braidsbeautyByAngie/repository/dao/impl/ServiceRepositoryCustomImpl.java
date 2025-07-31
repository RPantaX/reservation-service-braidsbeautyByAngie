package com.braidsbeautyByAngie.repository.dao.impl;

import com.braidsbeautyByAngie.aggregates.dto.PromotionDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestServiceFilter;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseCategoryWIthoutServices;
import com.braidsbeautyByAngie.aggregates.response.categories.ResponseSubCategory;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;
import com.braidsbeautyByAngie.entity.*;
import com.braidsbeautyByAngie.mapper.PromotionMapper;
import com.braidsbeautyByAngie.mapper.ServiceCategoryMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;
import com.braidsbeautyByAngie.repository.dao.ServiceRepositoryCustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ServiceRepositoryCustomImpl implements ServiceRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final PromotionMapper promotionMapper;
    @Transactional(readOnly = true)
    @Override
    public ResponseListPageableService filterServices(RequestServiceFilter filter) {
        log.info("Filtering services with parameters: {}", filter);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServiceEntity> query = cb.createQuery(ServiceEntity.class);
        Root<ServiceEntity> serviceRoot = query.from(ServiceEntity.class);

        // Joins necesarios
        Join<ServiceEntity, ServiceCategoryEntity> categoryJoin = serviceRoot.join("serviceCategoryEntity", JoinType.LEFT);
        Join<ServiceCategoryEntity, PromotionEntity> promotionJoin = categoryJoin.join("promotionEntities", JoinType.LEFT);
        Join<ServiceEntity, WorkServiceEntity> workServiceJoin = serviceRoot.join("workServices", JoinType.LEFT);
        Join<WorkServiceEntity, ScheduleEntity> scheduleJoin = workServiceJoin.join("scheduleEntity", JoinType.LEFT);

        // Lista de predicados para construir la consulta
        List<Predicate> predicates = new ArrayList<>();

        // Filtro base: solo servicios activos
        if (filter.getActiveOnly() == null || filter.getActiveOnly()) {
            predicates.add(cb.isTrue(serviceRoot.get("state")));
        }

        // Filtros de servicio principal
        addServiceFilters(cb, serviceRoot, predicates, filter);

        // Filtros de categoría
        addCategoryFilters(cb, categoryJoin, predicates, filter);

        // Filtros de precio y duración
        addPriceAndDurationFilters(cb, serviceRoot, predicates, filter);

        // Filtros de promociones
        addPromotionFilters(cb, promotionJoin, predicates, filter);

        // Filtros de disponibilidad
        addAvailabilityFilters(cb, scheduleJoin, workServiceJoin, predicates, filter);

        // Aplicar todos los predicados
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Distinct para evitar duplicados debido a los joins
        query.distinct(true);

        // Ordenamiento
        addOrderBy(cb, query, serviceRoot, filter);

        // Ejecutar consulta con paginación
        Pageable pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize());
        TypedQuery<ServiceEntity> typedQuery = entityManager.createQuery(query);

        // Calcular total de elementos para la paginación
        long totalElements = getTotalElements(filter);

        // Aplicar paginación
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<ServiceEntity> services = typedQuery.getResultList();

        // Convertir a DTOs
        List<ResponseService> responseServices = convertToResponseServices(services);

        // Crear página
        Page<ResponseService> page = new PageImpl<>(responseServices, pageable, totalElements);

        return ResponseListPageableService.builder()
                .responseServiceList(responseServices)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .end(page.isLast())
                .build();
    }
    private void addServiceFilters(CriteriaBuilder cb, Root<ServiceEntity> serviceRoot,
                                   List<Predicate> predicates, RequestServiceFilter filter) {

        // Filtro por nombre de servicio
        if (filter.getServiceName() != null && !filter.getServiceName().trim().isEmpty()) {
            predicates.add(cb.like(cb.upper(serviceRoot.get("serviceName")),
                    "%" + filter.getServiceName().toUpperCase() + "%"));
        }

        // Filtro por IDs de servicio
        if (filter.getServiceIds() != null && !filter.getServiceIds().isEmpty()) {
            predicates.add(serviceRoot.get("serviceId").in(filter.getServiceIds()));
        }

        // Filtro por descripción de servicio
        if (filter.getServiceDescription() != null && !filter.getServiceDescription().trim().isEmpty()) {
            predicates.add(cb.like(cb.upper(serviceRoot.get("serviceDescription")),
                    "%" + filter.getServiceDescription().toUpperCase() + "%"));
        }

        // Búsqueda general en nombre y descripción
        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            String searchPattern = "%" + filter.getSearchTerm().toUpperCase() + "%";
            Predicate nameSearch = cb.like(cb.upper(serviceRoot.get("serviceName")), searchPattern);
            Predicate descSearch = cb.like(cb.upper(serviceRoot.get("serviceDescription")), searchPattern);
            predicates.add(cb.or(nameSearch, descSearch));
        }
    }

    private void addCategoryFilters(CriteriaBuilder cb, Join<ServiceEntity, ServiceCategoryEntity> categoryJoin,
                                    List<Predicate> predicates, RequestServiceFilter filter) {

        // Filtro por IDs de categoría
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            predicates.add(categoryJoin.get("serviceCategoryId").in(filter.getCategoryIds()));
        }

        // Filtro por nombre de categoría
        if (filter.getCategoryName() != null && !filter.getCategoryName().trim().isEmpty()) {
            predicates.add(cb.like(cb.upper(categoryJoin.get("serviceCategoryName")),
                    "%" + filter.getCategoryName().toUpperCase() + "%"));
        }

        // Filtro por categoría padre
        if (filter.getParentCategoryId() != null) {
            predicates.add(cb.equal(categoryJoin.get("parentCategory").get("serviceCategoryId"),
                    filter.getParentCategoryId()));
        }

        // Solo categorías activas
        predicates.add(cb.isTrue(categoryJoin.get("state")));
    }

    private void addPriceAndDurationFilters(CriteriaBuilder cb, Root<ServiceEntity> serviceRoot,
                                            List<Predicate> predicates, RequestServiceFilter filter) {

        // Filtro por precio mínimo
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(serviceRoot.get("servicePrice"), filter.getMinPrice()));
        }

        // Filtro por precio máximo
        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(serviceRoot.get("servicePrice"), filter.getMaxPrice()));
        }

        // Filtro por duración mínima (LocalTime)
        if (filter.getMinDuration() != null) {
            predicates.add(cb.greaterThanOrEqualTo(serviceRoot.get("durationTimeAprox"), filter.getMinDuration()));
        }

        // Filtro por duración máxima (LocalTime)
        if (filter.getMaxDuration() != null) {
            predicates.add(cb.lessThanOrEqualTo(serviceRoot.get("durationTimeAprox"), filter.getMaxDuration()));
        }

        // Filtros alternativos por minutos (para facilidad del frontend)
        if (filter.getMinDurationMinutes() != null) {
            // Convertir minutos a LocalTime y comparar
            LocalTime minTime = LocalTime.of(filter.getMinDurationMinutes() / 60, filter.getMinDurationMinutes() % 60);
            predicates.add(cb.greaterThanOrEqualTo(serviceRoot.get("durationTimeAprox"), minTime));
        }

        if (filter.getMaxDurationMinutes() != null) {
            // Convertir minutos a LocalTime y comparar
            LocalTime maxTime = LocalTime.of(filter.getMaxDurationMinutes() / 60, filter.getMaxDurationMinutes() % 60);
            predicates.add(cb.lessThanOrEqualTo(serviceRoot.get("durationTimeAprox"), maxTime));
        }
    }

    private void addPromotionFilters(CriteriaBuilder cb, Join<ServiceCategoryEntity, PromotionEntity> promotionJoin,
                                     List<Predicate> predicates, RequestServiceFilter filter) {

        // Filtro solo servicios con promociones
        if (filter.getHasPromotion() != null && filter.getHasPromotion()) {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            predicates.add(cb.isNotNull(promotionJoin.get("promotionId")));
            predicates.add(cb.isTrue(promotionJoin.get("state")));
            predicates.add(cb.or(
                    cb.isNull(promotionJoin.get("promotionStartDate")),
                    cb.lessThanOrEqualTo(promotionJoin.get("promotionStartDate"), now)
            ));
            predicates.add(cb.or(
                    cb.isNull(promotionJoin.get("promotionEndDate")),
                    cb.greaterThanOrEqualTo(promotionJoin.get("promotionEndDate"), now)
            ));
        }

        // Filtro por IDs de promoción
        if (filter.getPromotionIds() != null && !filter.getPromotionIds().isEmpty()) {
            predicates.add(promotionJoin.get("promotionId").in(filter.getPromotionIds()));
        }

        // Filtro por tasa de descuento mínima
        if (filter.getMinDiscountRate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(promotionJoin.get("promotionDiscountRate"),
                    filter.getMinDiscountRate()));
        }

        // Filtro por tasa de descuento máxima
        if (filter.getMaxDiscountRate() != null) {
            predicates.add(cb.lessThanOrEqualTo(promotionJoin.get("promotionDiscountRate"),
                    filter.getMaxDiscountRate()));
        }
    }

    private void addAvailabilityFilters(CriteriaBuilder cb, Join<WorkServiceEntity, ScheduleEntity> scheduleJoin,
                                        Join<ServiceEntity, WorkServiceEntity> workServiceJoin,
                                        List<Predicate> predicates, RequestServiceFilter filter) {

        // Filtro solo servicios disponibles (con horarios activos)
        if (filter.getIsAvailable() != null && filter.getIsAvailable()) {
            predicates.add(cb.isNotNull(scheduleJoin.get("scheduleId")));
            predicates.add(cb.isTrue(scheduleJoin.get("state")));
            // Puedes agregar más lógica aquí para verificar disponibilidad real
            // por ejemplo, que la fecha del schedule sea futura
            predicates.add(cb.greaterThanOrEqualTo(scheduleJoin.get("scheduleDate"),
                    java.time.LocalDate.now()));
        }

        // Filtro por empleados específicos
        if (filter.getEmployeeIds() != null && !filter.getEmployeeIds().isEmpty()) {
            predicates.add(scheduleJoin.get("employeeId").in(filter.getEmployeeIds()));
        }

        // Filtro por estados de work service
        if (filter.getWorkServiceStates() != null && !filter.getWorkServiceStates().isEmpty()) {
            predicates.add(workServiceJoin.get("workServiceState").in(filter.getWorkServiceStates()));
        }

        // Solo work services activos
        predicates.add(cb.isTrue(workServiceJoin.get("state")));
    }

    private void addOrderBy(CriteriaBuilder cb, CriteriaQuery<ServiceEntity> query,
                            Root<ServiceEntity> serviceRoot, RequestServiceFilter filter) {

        Order order;
        boolean isAsc = "ASC".equalsIgnoreCase(filter.getSortDirection());

        switch (filter.getSortBy().toLowerCase()) {
            case "serviceprice":
            case "price":
                order = isAsc ? cb.asc(serviceRoot.get("servicePrice")) : cb.desc(serviceRoot.get("servicePrice"));
                break;
            case "durationtimeaprox":
            case "duration":
                order = isAsc ? cb.asc(serviceRoot.get("durationTimeAprox")) :
                        cb.desc(serviceRoot.get("durationTimeAprox"));
                break;
            case "createdat":
                order = isAsc ? cb.asc(serviceRoot.get("createdAt")) : cb.desc(serviceRoot.get("createdAt"));
                break;
            case "servicename":
            default:
                order = isAsc ? cb.asc(serviceRoot.get("serviceName")) : cb.desc(serviceRoot.get("serviceName"));
                break;
        }

        query.orderBy(order);
    }

    private long getTotalElements(RequestServiceFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ServiceEntity> serviceRoot = countQuery.from(ServiceEntity.class);

        // Mismos joins que en la consulta principal
        Join<ServiceEntity, ServiceCategoryEntity> categoryJoin = serviceRoot.join("serviceCategoryEntity", JoinType.LEFT);
        Join<ServiceCategoryEntity, PromotionEntity> promotionJoin = categoryJoin.join("promotionEntities", JoinType.LEFT);
        Join<ServiceEntity, WorkServiceEntity> workServiceJoin = serviceRoot.join("workServices", JoinType.LEFT);
        Join<WorkServiceEntity, ScheduleEntity> scheduleJoin = workServiceJoin.join("scheduleEntity", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // Aplicar todos los filtros
        if (filter.getActiveOnly() == null || filter.getActiveOnly()) {
            predicates.add(cb.isTrue(serviceRoot.get("state")));
        }

        addServiceFilters(cb, serviceRoot, predicates, filter);
        addCategoryFilters(cb, categoryJoin, predicates, filter);
        addPriceAndDurationFilters(cb, serviceRoot, predicates, filter);
        addPromotionFilters(cb, promotionJoin, predicates, filter);
        addAvailabilityFilters(cb, scheduleJoin, workServiceJoin, predicates, filter);

        if (!predicates.isEmpty()) {
            countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        countQuery.select(cb.countDistinct(serviceRoot.get("serviceId")));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<ResponseService> convertToResponseServices(List<ServiceEntity> services) {
        return services.stream().map(service -> {
            // Construir categoría con promociones
            ResponseCategoryWIthoutServices responseCategoryWIthoutServices = null;

            if (service.getServiceCategoryEntity() != null) {
                ServiceCategoryEntity serviceCategoryEntity = service.getServiceCategoryEntity();

                // Construir subcategorías
                List<ResponseSubCategory> subCategoryList = serviceCategoryEntity.getSubCategories() != null
                        ? serviceCategoryEntity.getSubCategories().stream()
                        .map(subCat -> {
                            ResponseSubCategory responseSubCategory = new ResponseSubCategory();
                            responseSubCategory.setServiceCategoryDTO(serviceCategoryMapper.mapServiceEntityToDTO(subCat));
                            return responseSubCategory;
                        }).collect(Collectors.toList())
                        : Collections.emptyList();

                // Construir promociones
                List<PromotionDTO> promotionDTOList = serviceCategoryEntity.getPromotionEntities() != null
                        ? promotionMapper.mapPromotionListToDtoList(serviceCategoryEntity.getPromotionEntities())
                        : Collections.emptyList();

                ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.mapServiceEntityToDTO(serviceCategoryEntity);

                responseCategoryWIthoutServices = ResponseCategoryWIthoutServices.builder()
                        .responseSubCategoryList(subCategoryList)
                        .serviceCategoryDTO(serviceCategoryDTO)
                        .promotionDTOList(promotionDTOList)
                        .build();
            }

            return ResponseService.builder()
                    .serviceDTO(serviceMapper.mapServiceEntityToDTO(service))
                    .responseCategoryWIthoutServices(responseCategoryWIthoutServices)
                    .build();
        }).collect(Collectors.toList());
    }
}
