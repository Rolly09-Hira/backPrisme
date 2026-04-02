package com.prisme.back.repository;

import com.prisme.back.entity.Inscription;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // Trouver par utilisateur
    List<Inscription> findByUtilisateur(Utilisateur utilisateur);
    List<Inscription> findByUtilisateurId(Long utilisateurId);

    // Trouver par formation
    List<Inscription> findByFormation(Formation formation);
    List<Inscription> findByFormationId(Long formationId);

    // Trouver par utilisateur et formation (une inscription spécifique)
    Optional<Inscription> findByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);

    // Trouver par statut
    List<Inscription> findByStatut(String statut);

    // Vérifier si un utilisateur est inscrit à une formation
    boolean existsByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);

    // Compter les inscriptions par formation
    Long countByFormationId(Long formationId);

    // Compter les inscriptions par statut et formation
    Long countByFormationIdAndStatut(Long formationId, String statut);

    // Trouver les inscriptions actives (non terminées)
    @Query("SELECT i FROM Inscription i WHERE i.statut != 'TERMINEE' AND i.statut != 'ABANDONNEE'")
    List<Inscription> findActiveInscriptions();

    // Trouver les inscriptions terminées avec certificat
    List<Inscription> findByCertificatObtenuTrue();

    // Trouver les inscriptions avec progression >= seuil
    List<Inscription> findByProgressionGreaterThanEqual(Integer progression);

    // Recherche avancée avec critères multiples
    @Query("SELECT i FROM Inscription i WHERE " +
            "(:utilisateurId IS NULL OR i.utilisateur.id = :utilisateurId) AND " +
            "(:formationId IS NULL OR i.formation.id = :formationId) AND " +
            "(:statut IS NULL OR i.statut = :statut) AND " +
            "(:certificatObtenu IS NULL OR i.certificatObtenu = :certificatObtenu)")
    List<Inscription> searchInscriptions(
            @Param("utilisateurId") Long utilisateurId,
            @Param("formationId") Long formationId,
            @Param("statut") String statut,
            @Param("certificatObtenu") Boolean certificatObtenu);

    // Statistiques par période
    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.dateInscription BETWEEN :debut AND :fin")
    Long countByDateInscriptionBetween(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Top formations les plus inscrites
    @Query("SELECT i.formation.titre, COUNT(i) as nbInscriptions " +
            "FROM Inscription i GROUP BY i.formation.titre ORDER BY nbInscriptions DESC")
    List<Object[]> findTopFormations();
}