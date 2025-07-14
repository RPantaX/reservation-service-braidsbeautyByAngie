package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.ports.in.ScheduleServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.auth.RequireRole;
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
                description = "Schedule management"
        )
)
@RestController
@RequestMapping("/v1/reservation-service/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleServiceIn scheduleServiceIn;

    @Operation(summary = "List all schedules")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listSchedulePageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(ApiResponse.ok("List schedule pageable", scheduleServiceIn.listScheduleByPageIn(pageNo, pageSize, sortBy, sortDir)));
    }

    @Operation(summary = "List schedule by id")
    @GetMapping(value = "/{scheduleId}")
    public ResponseEntity<ApiResponse> listScheduleById(@PathVariable(name = "scheduleId") Long scheduleId){
        return ResponseEntity.ok(ApiResponse.ok("List schedule by id", scheduleServiceIn.findScheduleByIdIn(scheduleId)));
    }

    @Operation(summary = "Save schedule")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @PostMapping()
    public ResponseEntity<ApiResponse> saveSchedule(@RequestBody RequestSchedule requestSchedule){
        return new ResponseEntity<>(ApiResponse.create("Save schedule", scheduleServiceIn.createScheduleIn(requestSchedule)), HttpStatus.CREATED);
    }

    @Operation(summary = "Update schedule")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse> updateSchedule(@PathVariable(name = "scheduleId") Long scheduleId,@RequestBody RequestSchedule requestSchedule){
        return new ResponseEntity<>(ApiResponse.create("Update schedule", scheduleServiceIn.updateScheduleIn(scheduleId, requestSchedule)), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete schedule")
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}) // Admin O Manager
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse> deleteSchedule(@PathVariable(name = "scheduleId") Long scheduleId){
        return ResponseEntity.ok(ApiResponse.ok("Delete schedule",scheduleServiceIn.deleteScheduleIn(scheduleId)));
    }

}
