package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Data
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column(nullable = false)
    private Double montant;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    private String statut;
    private String methode;

    @Column(name = "transaction_id")
    private String transactionId;

    private String devise;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "derniere_modification")
    private LocalDateTime derniereModification;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "valide_par")
    private String validePar;

    private String remarques;

    @ManyToOne
    @JoinColumn(name = "inscription_id", nullable = false)
    private Inscription inscription;

    @PrePersist
    protected void onCreate() {
        datePaiement = LocalDateTime.now();
        derniereModification = LocalDateTime.now();
        if (statut == null) statut = "EN_ATTENTE";
        if (devise == null) devise = "EUR";

        if (reference == null) {
            reference = "PAY-" + java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                    .format(LocalDateTime.now()) + "-" + (int)(Math.random() * 10000);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        derniereModification = LocalDateTime.now();
    }
}