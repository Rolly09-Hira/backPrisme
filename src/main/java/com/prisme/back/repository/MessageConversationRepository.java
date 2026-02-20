package com.prisme.back.repository;

import com.prisme.back.entity.MessageConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageConversationRepository extends JpaRepository<MessageConversation, Long> {

    List<MessageConversation> findByConversationIdOrderByDateEnvoiAsc(Long conversationId);

    @Query("SELECT m FROM MessageConversation m WHERE " +
            "m.conversation.id = :conversationId AND " +
            "m.destinataire.id = :destinataireId AND " +
            "m.lu = false")
    List<MessageConversation> findUnreadMessagesByConversationAndDestinataire(
            @Param("conversationId") Long conversationId,
            @Param("destinataireId") Long destinataireId
    );

    @Query("SELECT COUNT(m) FROM MessageConversation m WHERE " +
            "m.conversation.id = :conversationId AND " +
            "m.destinataire.id = :destinataireId AND " +
            "m.lu = false")
    long countUnreadMessagesByConversationAndDestinataire(
            @Param("conversationId") Long conversationId,
            @Param("destinataireId") Long destinataireId
    );

    @Query("SELECT m FROM MessageConversation m WHERE " +
            "m.expediteur.id = :userId OR m.destinataire.id = :userId " +
            "ORDER BY m.dateEnvoi DESC")
    List<MessageConversation> findAllUserMessages(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MessageConversation m WHERE " +
            "m.destinataire.id = :userId AND m.lu = false")
    long countTotalUnreadMessages(@Param("userId") Long userId);

    @Query("SELECT m FROM MessageConversation m WHERE " +
            "m.conversation.id = :conversationId " +
            "ORDER BY m.dateEnvoi DESC")
    List<MessageConversation> findLastMessageByConversation(
            @Param("conversationId") Long conversationId,
            org.springframework.data.domain.Pageable pageable
    );

    List<MessageConversation> findByConversationIdAndPieceJointeIsNotNull(Long conversationId);
}