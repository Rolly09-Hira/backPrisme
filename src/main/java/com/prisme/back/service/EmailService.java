package com.prisme.back.service;

import com.prisme.back.entity.MessageContact;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UtilisateurRepository utilisateurRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Prisme}")
    private String appName;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    public void sendContactConfirmation(MessageContact message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(message.getEmail());
            mailMessage.setSubject(String.format("Confirmation de réception - %s", appName));
            mailMessage.setText(String.format(
                    "Bonjour %s %s,\n\n" +
                            "Nous avons bien reçu votre message concernant '%s'.\n" +
                            "Notre équipe vous répondra dans les plus brefs délais.\n\n" +
                            "Votre message :\n%s\n\n" +
                            "Date d'envoi : %s\n\n" +
                            "Cordialement,\nL'équipe %s",
                    message.getPrenom(),
                    message.getNom(),
                    message.getSujet(),
                    message.getMessage(),
                    message.getDateEnvoi().format(DATE_FORMATTER),
                    appName
            ));

            mailSender.send(mailMessage);
            log.info("Email de confirmation envoyé à {}", message.getEmail());

        } catch (Exception e) {
            log.error("Erreur envoi confirmation: {}", e.getMessage());
        }
    }

    public void notifyAdminsNewContact(MessageContact message) {
        List<Utilisateur> admins = utilisateurRepository.findByRole("ADMIN");

        if (admins.isEmpty()) {
            log.warn("Aucun admin trouvé");
            return;
        }

        for (Utilisateur admin : admins) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(fromEmail);
                mailMessage.setTo(admin.getEmail());
                mailMessage.setSubject(String.format("[%s] Nouveau message - %s", appName, message.getSujet()));
                mailMessage.setText(String.format(
                        "Bonjour %s,\n\n" +
                                "Nouveau message de contact reçu :\n\n" +
                                "De : %s %s\n" +
                                "Email : %s\n" +
                                "Téléphone : %s\n" +
                                "Sujet : %s\n" +
                                "Date : %s\n\n" +
                                "Message :\n%s\n\n" +
                                "Connectez-vous pour répondre.\n\n" +
                                "Cordialement,\n%s",
                        admin.getPrenom(),
                        message.getPrenom(),
                        message.getNom(),
                        message.getEmail(),
                        message.getTelephone() != null ? message.getTelephone() : "Non fourni",
                        message.getSujet(),
                        message.getDateEnvoi().format(DATE_FORMATTER),
                        message.getMessage(),
                        appName
                ));

                mailSender.send(mailMessage);
                log.info("Notification envoyée à l'admin {}", admin.getEmail());

            } catch (Exception e) {
                log.error("Erreur notification admin: {}", e.getMessage());
            }
        }
    }

    public void sendContactResponse(MessageContact message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(message.getEmail());
            mailMessage.setSubject(String.format("Réponse à votre message - %s", message.getSujet()));
            mailMessage.setText(String.format(
                    "Bonjour %s %s,\n\n" +
                            "Notre équipe a répondu à votre message concernant '%s'.\n\n" +
                            "Votre message du %s :\n%s\n\n" +
                            "Notre réponse :\n%s\n\n" +
                            "Cordialement,\nL'équipe %s",
                    message.getPrenom(),
                    message.getNom(),
                    message.getSujet(),
                    message.getDateEnvoi().format(DATE_FORMATTER),
                    message.getMessage(),
                    message.getReponse(),
                    appName
            ));

            mailSender.send(mailMessage);
            log.info("Réponse envoyée à {}", message.getEmail());

        } catch (Exception e) {
            log.error("Erreur envoi réponse: {}", e.getMessage());
        }
    }

    public void notifyAdminResponseSent(MessageContact message, Long adminId) {
        utilisateurRepository.findById(adminId).ifPresent(admin -> {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(fromEmail);
                mailMessage.setTo(admin.getEmail());
                mailMessage.setSubject(String.format("Réponse envoyée - Message #%d", message.getId()));
                mailMessage.setText(String.format(
                        "Bonjour %s,\n\n" +
                                "Vous avez répondu au message de %s %s.\n" +
                                "Réponse envoyée à : %s\n\n" +
                                "Cordialement,\n%s",
                        admin.getPrenom(),
                        message.getPrenom(),
                        message.getNom(),
                        message.getEmail(),
                        appName
                ));

                mailSender.send(mailMessage);
                log.info("Confirmation envoyée à l'admin");

            } catch (Exception e) {
                log.error("Erreur confirmation admin: {}", e.getMessage());
            }
        });
    }
}