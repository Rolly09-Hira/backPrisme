package com.prisme.back.service;

import com.prisme.back.entity.*;
import com.prisme.back.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageConversationRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    @Transactional
    public MessageConversation envoyerMessage(
            Long conversationId,
            Long expediteurId,
            String contenu,
            TypeMessage type,
            String pieceJointe
    ) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        Utilisateur destinataire;
        if (expediteur.equals(conversation.getUtilisateur())) {
            destinataire = conversation.getAdmin();
        } else if (expediteur.equals(conversation.getAdmin())) {
            destinataire = conversation.getUtilisateur();
        } else {
            throw new RuntimeException("L'expéditeur ne fait pas partie de cette conversation");
        }

        MessageConversation message = new MessageConversation();
        message.setConversation(conversation);
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setContenu(contenu);
        message.setType(type != null ? type : TypeMessage.TEXTE);
        message.setPieceJointe(pieceJointe);

        MessageConversation savedMessage = messageRepository.save(message);

        conversation.setDernierMessage(savedMessage.getDateEnvoi());
        conversationRepository.save(conversation);

        log.info("Message envoyé dans la conversation {} par {}", conversationId, expediteurId);

        return savedMessage;
    }

    @Transactional
    public Conversation demarrerConversation(Long utilisateurId, Long adminId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Utilisateur admin = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

        return conversationRepository
                .findByUtilisateurAndAdmin(utilisateur, admin)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setUtilisateur(utilisateur);
                    conversation.setAdmin(admin);
                    Conversation saved = conversationRepository.save(conversation);
                    log.info("Nouvelle conversation créée entre {} et {}", utilisateurId, adminId);
                    return saved;
                });
    }

    public List<Conversation> getConversationsUtilisateur(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return conversationRepository.findByUtilisateurOrAdmin(utilisateur);
    }

    public List<MessageConversation> getMessagesConversation(Long conversationId) {
        return messageRepository.findByConversationIdOrderByDateEnvoiAsc(conversationId);
    }

    @Transactional
    public int marquerMessagesCommeLus(Long conversationId, Long destinataireId) {
        List<MessageConversation> messagesNonLus =
                messageRepository.findUnreadMessagesByConversationAndDestinataire(
                        conversationId, destinataireId
                );

        messagesNonLus.forEach(message -> {
            message.setLu(true);
            message.setDateLecture(LocalDateTime.now());
            messageRepository.save(message);
        });

        log.info("{} messages marqués comme lus dans la conversation {}",
                messagesNonLus.size(), conversationId);

        return messagesNonLus.size();
    }

    public long getNombreMessagesNonLus(Long conversationId, Long destinataireId) {
        return messageRepository.countUnreadMessagesByConversationAndDestinataire(
                conversationId, destinataireId
        );
    }

    @Transactional
    public void archiverConversation(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        conversation.setEstActive(false);
        conversationRepository.save(conversation);

        log.info("Conversation {} archivée", conversationId);
    }

    @Transactional
    public void reactiverConversation(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        conversation.setEstActive(true);
        conversationRepository.save(conversation);

        log.info("Conversation {} réactivée", conversationId);
    }
}