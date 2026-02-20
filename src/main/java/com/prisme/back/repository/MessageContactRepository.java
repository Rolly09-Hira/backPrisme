package com.prisme.back.repository;

import com.prisme.back.entity.MessageContact;
import com.prisme.back.entity.StatutContact;
import com.prisme.back.entity.CategorieSujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageContactRepository extends JpaRepository<MessageContact, Long> {

    List<MessageContact> findAllByOrderByDateEnvoiDesc();

    List<MessageContact> findByLuFalseOrderByDateEnvoiDesc();

    List<MessageContact> findByStatutOrderByDateEnvoiDesc(StatutContact statut);

    List<MessageContact> findByEmailOrderByDateEnvoiDesc(String email);

    List<MessageContact> findByCategorieOrderByDateEnvoiDesc(CategorieSujet categorie);

    @Query("SELECT COUNT(m) FROM MessageContact m WHERE m.lu = false")
    long countUnreadMessages();

    List<MessageContact> findByDateEnvoiBetweenOrderByDateEnvoiDesc(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT m.statut, COUNT(m) FROM MessageContact m GROUP BY m.statut")
    List<Object[]> countByStatut();

    @Query("SELECT COUNT(m) FROM MessageContact m WHERE m.telephone IS NOT NULL")
    long countByTelephoneIsNotNull();

    @Query("SELECT COUNT(m) FROM MessageContact m WHERE m.telephone IS NULL")
    long countByTelephoneIsNull();

    @Query("SELECT COUNT(m) FROM MessageContact m WHERE m.reponse IS NOT NULL")
    long countWithResponse();

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (m.date_reponse - m.date_envoi))/3600) FROM messages_contact m WHERE m.date_reponse IS NOT NULL", nativeQuery = true)
    Double averageResponseTimeHours();

    @Query("SELECT m FROM MessageContact m WHERE m.email = :email AND m.dateEnvoi > :since")
    List<MessageContact> findRecentByEmail(@Param("email") String email, @Param("since") LocalDateTime since);

    @Query("SELECT m FROM MessageContact m WHERE " +
            "LOWER(m.message) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "LOWER(m.sujet) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "LOWER(m.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "LOWER(m.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    List<MessageContact> rechercher(@Param("recherche") String recherche);
}