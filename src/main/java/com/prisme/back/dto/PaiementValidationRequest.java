package com.prisme.back.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class PaiementValidationRequest {

    @NotBlank(message = "Le statut est obligatoire")
    private String statut; // "VALIDE" ou "REJETE"

    private String remarques;

    @NotBlank(message = "Le validateur est obligatoire")
    private String validePar;
}