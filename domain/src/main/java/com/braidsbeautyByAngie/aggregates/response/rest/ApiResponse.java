package com.braidsbeautyByAngie.aggregates.response.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean error;
    private String code;
    private String message;
    private T data;
}
