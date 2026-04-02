package com.prisme.back.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequest {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;

    @NotBlank(message = "La méthode de paiement est obligatoire")
    private String methode;

    private String devise;
    private String description;

    @NotNull(message = "L'ID de l'inscription est obligatoire")
    private Long inscriptionId;

    // Optionnel : si paiement via transaction externe
    private String transactionId;
}