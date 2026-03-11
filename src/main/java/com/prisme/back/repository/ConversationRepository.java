package com.prisme.back.repository;

import com.prisme.back.entity.Conversation;
import com.prisme.back.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUtilisateurAndAdmin(
            Utilisateur utilisateur,
            Utilisateur admin
    );

    @Query("SELECT c FROM Conversation c WHERE " +
            "c.utilisateur = :user OR c.admin = :user " +
            "ORDER BY c.dernierMessage DESC")
    List<Conversation> findByUtilisateurOrAdmin(@Param("user") Utilisateur user);

    @Query("SELECT c FROM Conversation c WHERE " +
            "c.admin.id = :adminId AND c.estActive = true " +
            "ORDER BY c.dernierMessage DESC")
    List<Conversation> findActiveConversationsByAdmin(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE " +
            "c.utilisateur.id = :userId AND c.estActive = true")
    long countActiveUserConversations(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE " +
            "c.utilisateur.email LIKE %:email% OR c.admin.email LIKE %:email%")
    List<Conversation> findByUserEmailContaining(@Param("email") String email);

    @Query("SELECT c FROM Conversation c " +
            "LEFT JOIN FETCH c.messages m " +
            "WHERE c.id IN :conversationIds " +
            "ORDER BY c.dernierMessage DESC")
    List<Conversation> findConversationsWithMessages(@Param("conversationIds") List<Long> conversationIds);
}