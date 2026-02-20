package com.prisme.back.controller;

import com.prisme.back.dto.ChatMessageDTO;
import com.prisme.back.entity.*;
import com.prisme.back.repository.*;
import com.prisme.back.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageConversationRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    @MessageMapping("/chat/{conversationId}/send")
    public void sendMessage(
            @DestinationVariable Long conversationId,
            @Valid @Payload ChatMessageDTO messageDTO,
            Principal principal
    ) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));

        Utilisateur expediteur = utilisateurRepository.findById(messageDTO.getExpediteurId())
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        if (!expediteur.equals(conversation.getUtilisateur()) &&
                !expediteur.equals(conversation.getAdmin())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à envoyer des messages dans cette conversation");
        }

        Utilisateur destinataire = utilisateurRepository.findById(messageDTO.getDestinataireId())
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        MessageConversation message = new MessageConversation();
        message.setConversation(conversation);
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setContenu(messageDTO.getContenu());
        message.setType(messageDTO.getType());
        message.setPieceJointe(messageDTO.getPieceJointe());

        MessageConversation savedMessage = messageRepository.save(message);

        conversation.setDernierMessage(savedMessage.getDateEnvoi());
        conversationRepository.save(conversation);

        messagingTemplate.convertAndSend(
                "/topic/conversations/" + conversationId,
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                destinataire.getEmail(),
                "/queue/messages",
                savedMessage
        );

        notificationService.sendMessageNotification(destinataire, savedMessage);
    }

    @MessageMapping("/chat/{conversationId}/typing")
    public void userTyping(
            @DestinationVariable Long conversationId,
            @Payload String userEmail
    ) {
        messagingTemplate.convertAndSend(
                "/topic/conversations/" + conversationId + "/typing",
                Map.of("user", userEmail, "typing", true)
        );
    }

    @MessageMapping("/chat/{conversationId}/stoptyping")
    public void userStopTyping(
            @DestinationVariable Long conversationId,
            @Payload String userEmail
    ) {
        messagingTemplate.convertAndSend(
                "/topic/conversations/" + conversationId + "/typing",
                Map.of("user", userEmail, "typing", false)
        );
    }

    @MessageMapping("/chat/{conversationId}/read")
    public void markMessagesAsRead(
            @DestinationVariable Long conversationId,
            @Payload Long userId
    ) {
        List<MessageConversation> unreadMessages =
                messageRepository.findUnreadMessagesByConversationAndDestinataire(
                        conversationId, userId
                );

        unreadMessages.forEach(message -> {
            message.setLu(true);
            message.setDateLecture(LocalDateTime.now());
            messageRepository.save(message);
        });

        messagingTemplate.convertAndSend(
                "/topic/conversations/" + conversationId + "/read",
                Map.of("userId", userId, "count", unreadMessages.size())
        );
    }

    @SubscribeMapping("/user/queue/connect")
    public Map<String, Object> handleUserConnect(Principal principal) {
        if (principal != null) {
            notificationService.userConnected(principal.getName());

            return Map.of(
                    "status", "connected",
                    "user", principal.getName(),
                    "timestamp", LocalDateTime.now()
            );
        }
        return Map.of("status", "anonymous");
    }

    @RestController
    @RequestMapping("/api/chat")
    @RequiredArgsConstructor
    public static class ChatRestController {

        private final ConversationRepository conversationRepository;
        private final MessageConversationRepository messageRepository;
        private final UtilisateurRepository utilisateurRepository;

        @PostMapping("/conversation/start")
        // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<?> startConversation(
                @RequestParam Long utilisateurId,
                @RequestParam Long adminId
        ) {
            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Utilisateur admin = utilisateurRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

            Conversation conversation = conversationRepository
                    .findByUtilisateurAndAdmin(utilisateur, admin)
                    .orElseGet(() -> {
                        Conversation c = new Conversation();
                        c.setUtilisateur(utilisateur);
                        c.setAdmin(admin);
                        return conversationRepository.save(c);
                    });

            return ResponseEntity.ok(conversation);
        }

        @GetMapping("/conversations/user/{userId}")
        // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable Long userId) {
            Utilisateur utilisateur = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Conversation> conversations = conversationRepository.findByUtilisateurOrAdmin(utilisateur);

            for (Conversation conv : conversations) {
                long unreadCount = messageRepository.countUnreadMessagesByConversationAndDestinataire(
                        conv.getId(), userId
                );
                conv.setUnreadCount(unreadCount);
            }

            return ResponseEntity.ok(conversations);
        }

        @GetMapping("/conversation/{conversationId}/messages")
        // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<MessageConversation>> getConversationMessages(
                @PathVariable Long conversationId
        ) {
            List<MessageConversation> messages =
                    messageRepository.findByConversationIdOrderByDateEnvoiAsc(conversationId);
            return ResponseEntity.ok(messages);
        }

        @GetMapping("/conversation/{conversationId}/messages/paged")
        // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<List<MessageConversation>> getConversationMessagesPaged(
                @PathVariable Long conversationId,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "50") int size
        ) {
            PageRequest pageRequest = PageRequest.of(page, size);
            List<MessageConversation> messages =
                    messageRepository.findByConversationIdOrderByDateEnvoiAsc(conversationId);
            return ResponseEntity.ok(messages);
        }

        @GetMapping("/conversation/{conversationId}/unread/count")
        // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<Map<String, Long>> getUnreadCount(
                @PathVariable Long conversationId,
                @RequestParam Long userId
        ) {
            long count = messageRepository.countUnreadMessagesByConversationAndDestinataire(
                    conversationId, userId
            );

            return ResponseEntity.ok(Map.of("count", count));
        }

        @GetMapping("/user/{userId}/unread/total")
        // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
        public ResponseEntity<Map<String, Long>> getTotalUnreadCount(@PathVariable Long userId) {
            long count = messageRepository.countTotalUnreadMessages(userId);
            return ResponseEntity.ok(Map.of("total", count));
        }

        @PutMapping("/conversation/{conversationId}/archive")
        // @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> archiveConversation(@PathVariable Long conversationId) {
            return conversationRepository.findById(conversationId)
                    .map(conversation -> {
                        conversation.setEstActive(false);
                        conversationRepository.save(conversation);
                        return ResponseEntity.ok(Map.of("status", "Conversation archivée"));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }
}