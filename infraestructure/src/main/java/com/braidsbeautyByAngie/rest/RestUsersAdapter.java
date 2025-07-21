package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.dto.rest.EmployeeDto;
import com.braidsbeautyByAngie.aggregates.response.rest.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface RestUsersAdapter {

    @PostMapping("/v1/user-service/employee/list/by-ids")
    ApiResponse<List<EmployeeDto>> listReservationById(@RequestBody List<Long> employeeIds);
}
