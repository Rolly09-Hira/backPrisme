package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inscriptions")
@Data
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    private String statut;
    private Integer progression = 0;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "date_completion")
    private LocalDateTime dateCompletion;

    @Column(name = "certificat_obtenu")
    private Boolean certificatObtenu = false;

    private Double note;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    // Relations
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @OneToMany(mappedBy = "inscription", cascade = CascadeType.ALL)
    private List<Paiement> paiements;

    @PrePersist
    protected void onCreate() {
        dateInscription = LocalDateTime.now();
        if (statut == null) statut = "EN_ATTENTE";
        if (progression == null) progression = 0;
        if (certificatObtenu == null) certificatObtenu = false;
    }
}