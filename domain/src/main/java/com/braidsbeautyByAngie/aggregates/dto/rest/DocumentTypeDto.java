package com.braidsbeautyByAngie.aggregates.dto.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTypeDto {
    private Long id;

    @NotBlank
    private String value;
}