package com.prisme.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendActivationEmail(String to, String matricule, String password) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Activation de votre compte");

        message.setText(
                "Bonjour,\n\n" +
                        "Votre compte a été activé avec succès.\n\n" +
                        "Voici vos identifiants :\n\n" +
                        "Matricule : " + matricule + "\n" +
                        "Mot de passe : " + password + "\n\n" +
                        "Nous vous recommandons de changer votre mot de passe après votre première connexion.\n\n" +
                        "Cordialement,\nPrism Solutions"
        );

        mailSender.send(message);
    }
}
