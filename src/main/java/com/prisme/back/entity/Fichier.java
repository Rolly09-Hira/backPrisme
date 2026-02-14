package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "fichiers")
@Data
public class Fichier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String type; // "IMAGE", "VIDEO", "PDF", "DOC", "AUDIO"

    @Column(nullable = false)
    private String url;

    private Long taille;
    private String format;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;

    private String description;

    // Relations (modifiées - peut appartenir à un module OU à une formation)
    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module; // Pour les supports de cours

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation; // Pour les fichiers généraux de formation

    @PrePersist
    protected void onCreate() {
        dateUpload = LocalDateTime.now();
    }
}