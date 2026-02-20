package com.prisme.back.controller;

import com.prisme.back.dto.ContactMessageDTO;
import com.prisme.back.dto.ContactResponseDTO;
import com.prisme.back.entity.MessageContact;
import com.prisme.back.entity.StatutContact;
import com.prisme.back.repository.MessageContactRepository;
import com.prisme.back.service.EmailService;
import com.prisme.back.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final MessageContactRepository messageContactRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendContactMessage(
            @Valid @RequestBody ContactMessageDTO contactDTO,
            HttpServletRequest request
    ) {
        try {
            MessageContact message = new MessageContact();
            message.setNom(contactDTO.getNom());
            message.setPrenom(contactDTO.getPrenom());
            message.setEmail(contactDTO.getEmail());
            message.setTelephone(contactDTO.getTelephone());
            message.setSujet(contactDTO.getSujet());
            message.setMessage(contactDTO.getMessage());
            message.setCategorie(contactDTO.getCategorie());
            message.setIpAdresse(request.getRemoteAddr());
            message.setUserAgent(request.getHeader("User-Agent"));

            MessageContact savedMessage = messageContactRepository.save(message);

            messagingTemplate.convertAndSend(
                    "/topic/admin/contact-messages/new",
                    savedMessage
            );

            emailService.sendContactConfirmation(savedMessage);
            emailService.notifyAdminsNewContact(savedMessage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Votre message a été envoyé avec succès. Vous recevrez une réponse par email.");
            response.put("id", savedMessage.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de l'envoi du message: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/admin/messages")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MessageContact>> getAllContactMessages() {
        return ResponseEntity.ok(messageContactRepository.findAllByOrderByDateEnvoiDesc());
    }

    @GetMapping("/admin/messages/paged")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MessageContact>> getContactMessagesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("dateEnvoi").descending());
        return ResponseEntity.ok(messageContactRepository.findAll(pageRequest));
    }

    @GetMapping("/admin/messages/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageContact> getContactMessage(@PathVariable Long id) {
        return messageContactRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/messages/{id}/read")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return messageContactRepository.findById(id)
                .map(message -> {
                    message.setLu(true);
                    message.setDateLecture(LocalDateTime.now());
                    messageContactRepository.save(message);

                    messagingTemplate.convertAndSend(
                            "/topic/admin/contact-messages/" + id + "/read",
                            message
                    );

                    Map<String, String> response = new HashMap<>();
                    response.put("status", "Message marqué comme lu");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/messages/{id}/respond")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> respondToMessage(
            @PathVariable Long id,
            @Valid @RequestBody ContactResponseDTO responseDTO,
            @RequestAttribute("userId") Long adminId
    ) {
        return messageContactRepository.findById(id)
                .map(message -> {
                    if (message.getReponse() != null && !message.getReponse().isEmpty()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Ce message a déjà reçu une réponse");
                        return ResponseEntity.badRequest().body(error);
                    }

                    message.setReponse(responseDTO.getReponse());
                    message.setDateReponse(LocalDateTime.now());
                    message.setStatut(StatutContact.RESOLU);
                    message.setTraitePar(adminId);

                    MessageContact savedMessage = messageContactRepository.save(message);

                    emailService.sendContactResponse(savedMessage);
                    emailService.notifyAdminResponseSent(savedMessage, adminId);

                    messagingTemplate.convertAndSend(
                            "/topic/admin/contact-messages/" + id + "/responded",
                            savedMessage
                    );

                    messagingTemplate.convertAndSend(
                            "/topic/admin/contact-messages/updated",
                            savedMessage
                    );

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Réponse envoyée avec succès au visiteur par email");
                    response.put("email", message.getEmail());
                    response.put("dateReponse", message.getDateReponse());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/messages/{id}/note")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addInternalNote(
            @PathVariable Long id,
            @RequestBody ContactResponseDTO responseDTO,
            @RequestAttribute("userId") Long adminId
    ) {
        return messageContactRepository.findById(id)
                .map(message -> {
                    message.setReponse(responseDTO.getReponse());
                    message.setDateReponse(LocalDateTime.now());
                    message.setStatut(StatutContact.EN_COURS);
                    message.setTraitePar(adminId);

                    messageContactRepository.save(message);

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Note interne ajoutée, en attente d'envoi au visiteur");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/messages/{id}/resend")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resendResponse(@PathVariable Long id) {
        return messageContactRepository.findById(id)
                .map(message -> {
                    if (message.getReponse() == null || message.getReponse().isEmpty()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Aucune réponse à renvoyer");
                        return ResponseEntity.badRequest().body(error);
                    }

                    emailService.sendContactResponse(message);

                    Map<String, String> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Réponse renvoyée avec succès à " + message.getEmail());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/messages/{id}/status")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusMap
    ) {
        return messageContactRepository.findById(id)
                .map(message -> {
                    StatutContact newStatus = StatutContact.valueOf(statusMap.get("statut"));
                    message.setStatut(newStatus);
                    messageContactRepository.save(message);

                    Map<String, String> response = new HashMap<>();
                    response.put("status", "Statut mis à jour: " + newStatus);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/stats")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", messageContactRepository.count());
        stats.put("nonLus", messageContactRepository.countUnreadMessages());

        List<Object[]> byStatut = messageContactRepository.countByStatut();
        Map<String, Long> statutMap = new HashMap<>();
        for (Object[] row : byStatut) {
            statutMap.put(((StatutContact) row[0]).name(), (Long) row[1]);
        }
        stats.put("parStatut", statutMap);

        stats.put("avecTelephone", messageContactRepository.countByTelephoneIsNotNull());
        stats.put("sansTelephone", messageContactRepository.countByTelephoneIsNull());
        stats.put("avecReponse", messageContactRepository.countWithResponse());

        Double tempsMoyen = messageContactRepository.averageResponseTimeHours();
        stats.put("tempsMoyenReponseHeures", tempsMoyen != null ? tempsMoyen : 0);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/search")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MessageContact>> searchMessages(
            @RequestParam String q
    ) {
        return ResponseEntity.ok(messageContactRepository.rechercher(q));
    }

    @GetMapping("/admin/export/csv")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportToCsv() {
        List<MessageContact> messages = messageContactRepository.findAllByOrderByDateEnvoiDesc();

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Date,Nom,Prénom,Email,Téléphone,Sujet,Message,Statut,Réponse,Date Réponse\n");

        for (MessageContact msg : messages) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%s,\"%s\",%s,\"%s\",%s\n",
                    msg.getId(),
                    msg.getDateEnvoi(),
                    msg.getNom(),
                    msg.getPrenom(),
                    msg.getEmail(),
                    msg.getTelephone() != null ? msg.getTelephone() : "",
                    msg.getSujet(),
                    msg.getMessage().replace("\"", "\"\""),
                    msg.getStatut(),
                    msg.getReponse() != null ? msg.getReponse().replace("\"", "\"\"") : "",
                    msg.getDateReponse() != null ? msg.getDateReponse() : ""
            ));
        }

        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=messages_contact.csv")
                .body(csv.toString());
    }
}