package com.prisme.back.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDTO {
    private Long id;
    private String reference;
    private Double montant;
    private LocalDateTime datePaiement;
    private String statut;
    private String methode;
    private String transactionId;
    private String devise;
    private String description;
    private LocalDateTime derniereModification;
    private LocalDateTime dateValidation;
    private String validePar;
    private String remarques;


    private Long inscriptionId;
    private String inscriptionReference;
    private String utilisateurNom;
    private String formationTitre;
}