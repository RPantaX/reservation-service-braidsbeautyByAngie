package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseListPageableWorkService;
import com.braidsbeautyByAngie.ports.in.WorkServiceServiceIn;
import com.braidsbeautybyangie.coreservice.aggregates.Constants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@OpenAPIDefinition(
        info = @Info(
                title = "API-CATEGORY",
                version = "1.0",
                description = "WorkService management"
        )
)
@RestController
@RequestMapping("/v1/reservation-service/workservice")
@RequiredArgsConstructor
public class WorkServiceController {
    private final WorkServiceServiceIn workServiceService;

    @Operation(summary = "List all workservices")
    @GetMapping("/list")
    public ResponseEntity<ResponseListPageableWorkService> listWorkServicePageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                                       @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                                       @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                                       @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(workServiceService.listWorkServiceByPageIn(pageNo, pageSize, sortBy, sortDir));
    }
    @Operation(summary = "Cancel workservice")
    @DeleteMapping("/{workServiceId}")
    public ResponseEntity<WorkServiceDTO> cancelWorkService(@PathVariable(name = "workServiceId") Long workServiceId){
        return ResponseEntity.ok(workServiceService.cancelWorkServiceIn(workServiceId));
    }
}
