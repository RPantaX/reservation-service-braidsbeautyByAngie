package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseListPageableService;
import com.braidsbeautyByAngie.aggregates.response.services.ResponseService;
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
    @PostMapping()
    public ResponseEntity<ApiResponse> saveService(@RequestBody RequestService requestService){
        return new ResponseEntity<>(ApiResponse.create( "service saved",serviceService.createServiceIn(requestService)), HttpStatus.CREATED);
    }

    @Operation(summary = "Update service")
    @PutMapping("/{serviceId}")
    public ResponseEntity<ApiResponse> updateService(@PathVariable(name = "serviceId") Long serviceId,@RequestBody RequestService requestService){
        return new ResponseEntity<>(ApiResponse.create("Updated service" , serviceService.updateServiceIn(serviceId, requestService)), HttpStatus.CREATED);
    }
    @Operation(summary = "Delete service")
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

}
