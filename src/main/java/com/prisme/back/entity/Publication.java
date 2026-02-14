package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "publications")
@Data
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    private String image;
    private String video;
    private String categorie;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    private String auteur;
    private String source;

    @Column(name = "lien_externe")
    private String lienExterne;

    private Integer vu = 0;
    private Boolean publie = true;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @PrePersist
    protected void onCreate() {
        datePublication = LocalDateTime.now();
        if (vu == null) vu = 0;
        if (publie == null) publie = true;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}