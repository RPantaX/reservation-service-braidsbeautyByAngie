package com.braidsbeautyByAngie.aggregates.dto.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    private String phoneNumber;

    @Email
    private String emailAddress;

    // Relaciones como IDs
    private Long addressId;
    private Long documentTypeId;

    // Objetos anidados para respuestas completas
    private AddressDto address;
    private DocumentTypeDto documentType;

    // Campo calculado para nombre completo
    public String getFullName() {
        return (name != null ? name : "") + " " + (lastName != null ? lastName : "");
    }
}