package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.request.RequestServiceFilter;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;
import com.braidsbeautyByAngie.aggregates.response.services.ServiceFilterOptionsDTO;
import com.braidsbeautyByAngie.auth.RequireRole;
import com.braidsbeautyByAngie.ports.in.ServiceServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@OpenAPIDefinition(
        info = @Info(
                title = "API-CATEGORY",
                version = "1.0",
                description = "Service management"
        )
)
@RestController
@RequestMapping("/v1/reservation-service/service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceServiceIn serviceService;

    @Operation(summary = "List all services")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listServicePageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                               @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                               @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                               @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(ApiResponse.ok("List all services pageable",serviceService.listServiceByPageIn(pageNo, pageSize, sortBy, sortDir)));
    }
    @Operation(summary = "List service by id")
    @GetMapping(value = "/{serviceId}")
    public ResponseEntity<ApiResponse> listServiceById(@PathVariable(name = "serviceId") Long serviceId){
        return ResponseEntity.ok(ApiResponse.ok("List service by id",serviceService.findServiceByIdIn(serviceId)));
    }
    @Operation(summary = "Save service")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @PostMapping()
    public ResponseEntity<ApiResponse> saveService(@RequestBody RequestService requestService){
        return new ResponseEntity<>(ApiResponse.create( "service saved",serviceService.createServiceIn(requestService)), HttpStatus.CREATED);
    }
    @Operation(summary = "Update service")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @PutMapping("/{serviceId}")
    public ResponseEntity<ApiResponse> updateService(@PathVariable(name = "serviceId") Long serviceId,@RequestBody RequestService requestService){
        return new ResponseEntity<>(ApiResponse.create("Updated service" , serviceService.updateServiceIn(serviceId, requestService)), HttpStatus.CREATED);
    }
    @Operation(summary = "Delete service")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_SUPER_ADMIN"}, requireAll = true) // Admin Y Super Admin
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse> deleteService(@PathVariable(name = "serviceId") Long serviceId){
        return ResponseEntity.ok(ApiResponse.ok("Delete service", serviceService.deleteServiceIn(serviceId)));
    }

    @Operation(summary = "List all services by category")
    @GetMapping("/list/category/{categoryId}")
    public ResponseEntity<ApiResponse> listServiceByPageByCategory(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                                     @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                                     @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                                     @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir,
                                                                                     @PathVariable(name = "categoryId") Long categoryId){
        return ResponseEntity.ok(ApiResponse.ok("list service by category", serviceService.listServiceByPageByCategoryIn(pageNo, pageSize, sortBy, sortDir, categoryId)));
    }
    @Operation(summary = "Filter services with advanced criteria")
    @PostMapping("/filter")
    public ResponseEntity<ApiResponse> filterServices(
            @RequestBody RequestServiceFilter filter) {

        ResponseListPageableService response = serviceService.filterServicesIn(filter);

        return ResponseEntity.ok(ApiResponse.ok("Services filtered successfully", response));
    }

    @Operation(summary = "Search services with simple parameters")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchServices(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) Integer minDurationMinutes,
            @RequestParam(required = false) Integer maxDurationMinutes,
            @RequestParam(required = false) Boolean hasPromotion,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String employeeIds, // comma-separated
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "serviceName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        // Convertir employeeIds de string a lista
        List<Long> employeeIdList = null;
        if (employeeIds != null && !employeeIds.trim().isEmpty()) {
            employeeIdList = Arrays.stream(employeeIds.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();
        }

        RequestServiceFilter filter = RequestServiceFilter.builder()
                .searchTerm(searchTerm)
                .categoryIds(categoryId != null ? List.of(categoryId) : null)
                .minPrice(minPrice != null ? new BigDecimal(minPrice) : null)
                .maxPrice(maxPrice != null ? new BigDecimal(maxPrice) : null)
                .minDurationMinutes(minDurationMinutes)
                .maxDurationMinutes(maxDurationMinutes)
                .hasPromotion(hasPromotion)
                .isAvailable(isAvailable)
                .employeeIds(employeeIdList)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        ResponseListPageableService response = serviceService.filterServicesIn(filter);

        return ResponseEntity.ok(ApiResponse.ok("Services searched successfully", response));
    }

    @Operation(summary = "Get services by category with additional filters")
    @GetMapping("/category/{categoryId}/filter")
    public ResponseEntity<ApiResponse> getServicesByCategoryWithFilters(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) Boolean hasPromotion,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "serviceName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        RequestServiceFilter filter = RequestServiceFilter.builder()
                .categoryIds(List.of(categoryId))
                .minPrice(minPrice != null ? new BigDecimal(minPrice) : null)
                .maxPrice(maxPrice != null ? new BigDecimal(maxPrice) : null)
                .hasPromotion(hasPromotion)
                .isAvailable(isAvailable)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        ResponseListPageableService response = serviceService.filterServicesIn(filter);

        return ResponseEntity.ok(ApiResponse.ok("Services by category filtered successfully", response));
    }

    @Operation(summary = "Get available services for a specific employee")
    @GetMapping("/employee/{employeeId}/available")
    public ResponseEntity<ApiResponse> getAvailableServicesByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        RequestServiceFilter filter = RequestServiceFilter.builder()
                .employeeIds(List.of(employeeId))
                .isAvailable(true)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy("serviceName")
                .sortDirection("ASC")
                .build();

        ResponseListPageableService response = serviceService.filterServicesIn(filter);

        return ResponseEntity.ok(ApiResponse.ok("Available services for employee", response));
    }

    @Operation(summary = "Get services with active promotions")
    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse> getServicesWithPromotions(
            @RequestParam(required = false) String minDiscountRate,
            @RequestParam(required = false) String maxDiscountRate,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {

        RequestServiceFilter filter = RequestServiceFilter.builder()
                .hasPromotion(true)
                .minDiscountRate(minDiscountRate != null ? new BigDecimal(minDiscountRate) : null)
                .maxDiscountRate(maxDiscountRate != null ? new BigDecimal(maxDiscountRate) : null)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy("servicePrice")
                .sortDirection("ASC")
                .build();

        ResponseListPageableService response = serviceService.filterServicesIn(filter);

        return ResponseEntity.ok(ApiResponse.ok("Services with promotions", response));
    }
    @Operation(summary = "Get service filter options",
            description = "Returns all available filter options for services including categories, price ranges, duration ranges, and available employees")
    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse> getServiceFilterOptions() {
            ServiceFilterOptionsDTO filterOptions = serviceService.getServiceFilterOptionsIn();

            return ResponseEntity.ok(ApiResponse.ok(
                    "Service filter options retrieved successfully",
                    filterOptions
            ));
    }
}
