package com.prisme.back.repository;

import com.prisme.back.entity.Paiement;
import com.prisme.back.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    // Trouver par référence (unique)
    Optional<Paiement> findByReference(String reference);

    // Trouver par inscription
    List<Paiement> findByInscription(Inscription inscription);
    List<Paiement> findByInscriptionId(Long inscriptionId);

    // Trouver par statut
    List<Paiement> findByStatut(String statut);

    // Trouver par méthode de paiement
    List<Paiement> findByMethode(String methode);

    // Trouver par transaction ID
    Optional<Paiement> findByTransactionId(String transactionId);

    // Compter les paiements par statut pour une inscription
    Long countByInscriptionIdAndStatut(Long inscriptionId, String statut);

    // Somme des montants par inscription
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.inscription.id = :inscriptionId AND p.statut = 'VALIDE'")
    Double sumMontantValideByInscriptionId(@Param("inscriptionId") Long inscriptionId);

    // Paiements entre deux dates
    List<Paiement> findByDatePaiementBetween(LocalDateTime debut, LocalDateTime fin);

    // Paiements par statut et période
    @Query("SELECT p FROM Paiement p WHERE p.statut = :statut AND p.datePaiement BETWEEN :debut AND :fin")
    List<Paiement> findByStatutAndDatePaiementBetween(
            @Param("statut") String statut,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Paiements validés par un utilisateur
    List<Paiement> findByValidePar(String validePar);

    // Vérifier si une référence existe
    boolean existsByReference(String reference);

    // Statistiques mensuelles
    @Query("SELECT FUNCTION('MONTH', p.datePaiement), COUNT(p), SUM(p.montant) " +
            "FROM Paiement p WHERE p.statut = 'VALIDE' AND YEAR(p.datePaiement) = :annee " +
            "GROUP BY FUNCTION('MONTH', p.datePaiement)")
    List<Object[]> getMonthlyStats(@Param("annee") Integer annee);

    // Paiements en attente de validation
    List<Paiement> findByStatutAndDatePaiementBefore(String statut, LocalDateTime date);
}