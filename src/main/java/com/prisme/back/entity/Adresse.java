package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "adresses")
@Data
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private String complement;

    @OneToOne
    @JoinColumn(name = "utilisateur_id", unique = true)
    private Utilisateur utilisateur;
}