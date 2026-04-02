package com.prisme.back.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionRequest {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;

    @Min(value = 0, message = "La progression doit être entre 0 et 100")
    @Max(value = 100, message = "La progression doit être entre 0 et 100")
    private Integer progression;

    private String commentaire;
}