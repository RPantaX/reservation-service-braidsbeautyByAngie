package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.auth.RequireRole;
import com.braidsbeautyByAngie.ports.in.ScheduleServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
    @GetMapping("/list/pageable")
    public ResponseEntity<ApiResponse> listSchedulePageableList(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(ApiResponse.ok("List schedule pageable", scheduleServiceIn.listScheduleByPageIn(pageNo, pageSize, sortBy, sortDir)));
    }
    @Operation(summary = "List schedule by id")
    @GetMapping(value = "/list")
    public ResponseEntity<ApiResponse> listSchedules(){
        return ResponseEntity.ok(ApiResponse.ok("List schedule with employees", scheduleServiceIn.listScheduleIn()));
    }

    // NUEVO: Filtrar desde una fecha específica
    @GetMapping("/list/from-date")
    public ResponseEntity<ApiResponse> listSchedulesFromDate(
            @RequestParam(value = "fromDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        return ResponseEntity.ok(ApiResponse.ok("Schedules from date retrieved successfully",
                scheduleServiceIn.listScheduleFromDateIn(fromDate)));
    }

    // NUEVO: Filtrar por rango de fechas
    @GetMapping("/list/between-dates")
    public ResponseEntity<ApiResponse> listSchedulesBetweenDates(
            @RequestParam(value = "fromDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(ApiResponse.ok("Schedules between dates retrieved successfully",
                scheduleServiceIn.listScheduleBetweenDatesIn(fromDate, toDate)));
    }

    // NUEVO: Filtrar por fecha específica
    @GetMapping("/list/by-date")
    public ResponseEntity<ApiResponse> listSchedulesBySpecificDate(
            @RequestParam(value = "date", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate) {
        return ResponseEntity.ok(ApiResponse.ok("Schedules for specific date retrieved successfully",
                scheduleServiceIn.listScheduleBySpecificDateIn(scheduleDate)));
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
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse> createSchedulesBulk(@RequestBody List<RequestSchedule> requestSchedules) {
        List<ScheduleDTO> createdSchedules = scheduleServiceIn.createSchedulesBulkIn(requestSchedules);
        return new ResponseEntity<>(ApiResponse.create("Save schedules", createdSchedules), HttpStatus.CREATED);
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
