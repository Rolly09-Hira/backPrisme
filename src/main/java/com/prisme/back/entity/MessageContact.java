package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages_contact")
@Data
public class MessageContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Informations de contact
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    private String telephone;

    // Sujet/Objet du message
    @Column(nullable = false)
    private String sujet;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    // Métadonnées
    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "ip_adresse")
    private String ipAdresse;

    @Column(name = "user_agent")
    private String userAgent;

    // Gestion par l'admin
    private Boolean lu = false;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Enumerated(EnumType.STRING)
    private StatutContact statut = StatutContact.NOUVEAU;

    @Column(columnDefinition = "TEXT")
    private String reponse;

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @Column(name = "traite_par")
    private Long traitePar;

    @Enumerated(EnumType.STRING)
    private CategorieSujet categorie;

    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
        if (lu == null) lu = false;
        if (statut == null) statut = StatutContact.NOUVEAU;
    }
}