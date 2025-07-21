package com.braidsbeautyByAngie.aggregates.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
    private String employeeImage;

    // Relaciones como IDs para requests
    private Long employeeTypeId;
    private Long userId;
    private Long personId;

    // Objetos anidados para respuestas completas
    private EmployeeTypeDto employeeType;
    private PersonDto person;
}