package com.braidsbeautyByAngie.aggregates.request;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestServiceFilter {

    // Filtros de servicio principal
    private String serviceName;
    private List<Long> serviceIds;
    private String serviceDescription;

    // Filtros de categoría
    private List<Long> categoryIds;
    private String categoryName;
    private Long parentCategoryId; // Para filtrar por categoría padre

    // Filtros de precio
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Filtros de duración del servicio
    private LocalTime minDuration; // Duración mínima del servicio
    private LocalTime maxDuration; // Duración máxima del servicio
    private Integer minDurationMinutes; // Alternativa en minutos para facilidad del frontend
    private Integer maxDurationMinutes; // Alternativa en minutos para facilidad del frontend

    // Filtros de promociones
    private Boolean hasPromotion; // true = solo servicios con promociones activas
    private List<Long> promotionIds;
    private BigDecimal minDiscountRate;
    private BigDecimal maxDiscountRate;

    // Filtros de disponibilidad (basado en schedules)
    private Boolean isAvailable; // true = solo servicios con horarios disponibles
    private List<Long> employeeIds; // Filtrar por empleados específicos

    // Filtros de búsqueda general
    private String searchTerm; // Busca en nombre y descripción del servicio

    // Filtros de estado
    private Boolean activeOnly; // Por defecto true

    // Filtros avanzados
    private List<String> workServiceStates; // Estados de work_service si necesitas filtrar por eso

    // Paginación
    private int pageNumber = 0;
    private int pageSize = 10;

    // Ordenamiento
    private String sortBy = "serviceName"; // serviceName, servicePrice, durationTimeAprox, createdAt
    private String sortDirection = "ASC"; // ASC, DESC
}
