package com.prisme.back.service;

import com.prisme.back.entity.MessageConversation;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UtilisateurRepository utilisateurRepository;

    private final Map<String, UserConnection> connectedUsers = new ConcurrentHashMap<>();

    public void userConnected(String email) {
        connectedUsers.put(email, new UserConnection(email, LocalDateTime.now(), true));
        broadcastUserStatus(email, true);
        log.info("Utilisateur connecté: {}", email);
    }

    public void userDisconnected(String email) {
        connectedUsers.remove(email);
        broadcastUserStatus(email, false);
        log.info("Utilisateur déconnecté: {}", email);
    }

    private void broadcastUserStatus(String email, boolean online) {
        messagingTemplate.convertAndSend(
                "/topic/user-status",
                Map.of(
                        "email", email,
                        "online", online,
                        "timestamp", LocalDateTime.now()
                )
        );
    }

    public void sendMessageNotification(Utilisateur destinataire, MessageConversation message) {
        if (connectedUsers.containsKey(destinataire.getEmail())) {
            String content = message.getContenu();
            String preview = content.length() > 50 ? content.substring(0, 50) + "..." : content;

            messagingTemplate.convertAndSendToUser(
                    destinataire.getEmail(),
                    "/queue/notifications",
                    Map.of(
                            "type", "NEW_MESSAGE",
                            "conversationId", message.getConversation().getId(),
                            "messageId", message.getId(),
                            "from", message.getExpediteur().getPrenom() + " " + message.getExpediteur().getNom(),
                            "content", preview,
                            "timestamp", LocalDateTime.now()
                    )
            );
            log.debug("Notification envoyée à {}", destinataire.getEmail());
        }
    }

    public boolean isUserOnline(String email) {
        return connectedUsers.containsKey(email);
    }

    public Map<String, UserConnection> getAllConnectedUsers() {
        return connectedUsers;
    }

    // Classe interne avec annotations Lombok séparées
    @lombok.Getter
    @lombok.Setter
    @lombok.AllArgsConstructor
    public static class UserConnection {
        private String email;
        private LocalDateTime connectedSince;
        private boolean online;
    }
}