package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    private Boolean lu = false;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "piece_jointe")
    private String pieceJointe;

    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
        if (lu == null) lu = false;
    }
}