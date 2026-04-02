package com.prisme.back.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDTO {
    private Long id;
    private LocalDateTime dateInscription;
    private String statut;
    private Integer progression;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private LocalDateTime dateCompletion;
    private Boolean certificatObtenu;
    private Double note;
    private String commentaire;

    // Informations utilisateur simplifiées
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurEmail;

    // Informations formation simplifiées
    private Long formationId;
    private String formationTitre;
    private String formationDescription;
    private Double formationPrix;

    // Paiements associés
    private List<PaiementDTO> paiements;
}