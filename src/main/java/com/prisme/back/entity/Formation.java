package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "formations")
@Data
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double prix;
    private String categorie;
    private String image;
    private String duree;
    private String langue;
    private Boolean certificat = false;

    @Column(columnDefinition = "TEXT")
    private String programme;

    @Column(columnDefinition = "TEXT")
    private String prerequis;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    private Boolean actif = true;

    // Relations
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
    private List<Matiere> matieres; // Nouvelle relation

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (certificat == null) certificat = false;
        if (actif == null) actif = true;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}