package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Data
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String role;
    private String telephone;

    @Column(name = "date_naissance")
    private LocalDateTime dateNaissance;

    private String sexe;

    @Column(name = "photo_profil")
    private String photoProfil;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String statut;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    private Boolean actif = true;

    @Column(name = "email_verifie")
    private Boolean emailVerifie = false;

    @Column(name = "telephone_verifie")
    private Boolean telephoneVerifie = false;

    // Relations
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Adresse adresse;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    private List<Message> messagesEnvoyes;

    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL)
    private List<Message> messagesRecus;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (role == null) role = "VISITEUR";
        if (statut == null) statut = "ACTIF";
        if (actif == null) actif = true;
        if (emailVerifie == null) emailVerifie = false;
        if (telephoneVerifie == null) telephoneVerifie = false;
    }
}