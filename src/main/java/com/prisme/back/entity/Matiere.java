package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matieres")
@Data
public class Matiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;
    private Integer ordre; // Ordre d'affichage dans la formation

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    // Relations
    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @OneToMany(mappedBy = "matiere", cascade = CascadeType.ALL)
    private List<Module> modules;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}