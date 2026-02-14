package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "modules")
@Data
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String description;
    private Integer dureeEstimee; // en minutes
    private Integer ordre; // Ordre dans la matière

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    // Relations
    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Fichier> fichiers; // Supports de cours

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}