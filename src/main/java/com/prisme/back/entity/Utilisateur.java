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

    private Boolean actif = false;

    @Column(name = "photo_profil")
    private String photoProfil;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "expediteur")
    private List<MessageConversation> messagesEnvoyes;

    @OneToMany(mappedBy = "destinataire")
    private List<MessageConversation> messagesRecus;

    @OneToMany(mappedBy = "utilisateur")
    private List<Conversation> conversationsUtilisateur;

    @OneToMany(mappedBy = "admin")
    private List<Conversation> conversationsAdmin;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (role == null) role = "UTILISATEUR";
        if (actif == null) actif = false;
    }
}