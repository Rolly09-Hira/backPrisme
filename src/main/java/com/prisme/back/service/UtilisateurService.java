package com.prisme.back.service;

import com.prisme.back.dto.*;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.exception.InvalidCredentialsException;
import com.prisme.back.repository.UtilisateurRepository;
import com.prisme.back.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public EmailCheckResponse checkEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        return new EmailCheckResponse(existe);
    }
    public Utilisateur register(String nom,
                                String prenom,
                                String email,
                                String motDePasse,
                                String role,
                                String telephone,
                                LocalDate dateNaissance,
                                MultipartFile photo) {

        if (repository.existsByEmail(email)) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Utilisateur user = new Utilisateur();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        if (motDePasse != null && !motDePasse.isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(motDePasse));
        } else {
            user.setMotDePasse(null);
        }
        user.setRole("ADMIN".equals(role) ? "ADMIN" : "UTILISATEUR");
        user.setTelephone(telephone);
        user.setDateNaissance(dateNaissance);
        user.setActif(false);

        // Upload photo
        if (photo != null && !photo.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/profiles/";

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                File destination = new File(uploadDir + fileName);
                photo.transferTo(destination);

                user.setPhotoProfil("/uploads/profiles/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'upload de la photo", e);
            }
        }

        return repository.save(user); // ⬅ retourne l'utilisateur, pas de token
    }

    public AuthResponse login(String email, String motDePasse) {

        // Vérification utilisateur
        Utilisateur user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }

        // Génération token JWT
        String token = jwtService.generateToken(user);

        // Retourner token + infos utilisateur
        return new AuthResponse(token, user.getNom(), user.getEmail(), user.getPhotoProfil());
    }

    public List<UtilisateurDTO> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(u -> {

                    AdresseDTO adresseDTO = null;

                    if (u.getAdresse() != null) {
                        adresseDTO = new AdresseDTO(
                                u.getAdresse().getId(),
                                u.getAdresse().getRue(),
                                u.getAdresse().getVille(),
                                u.getAdresse().getCodePostal(),
                                u.getAdresse().getPays(),
                                u.getAdresse().getComplement()
                        );
                    }

                    return new UtilisateurDTO(
                            u.getId(),
                            u.getNom(),
                            u.getPrenom(),
                            u.getEmail(),
                            u.getTelephone(),
                            u.getDateNaissance(),
                            u.getPhotoProfil(),
                            adresseDTO
                    );
                })
                .collect(Collectors.toList());
    }


    public Utilisateur updateUser(Long id, Utilisateur updatedUser) {
        Utilisateur user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setNom(updatedUser.getNom());
        user.setPrenom(updatedUser.getPrenom());
        user.setTelephone(updatedUser.getTelephone());
        user.setDateNaissance(updatedUser.getDateNaissance());
        user.setRole(updatedUser.getRole());

        return repository.save(user);
    }

    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        repository.deleteById(id);
    }

    public Utilisateur activateUser(Long id) {

        Utilisateur user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (Boolean.TRUE.equals(user.getActif())) {
            throw new RuntimeException("Compte déjà activé");
        }

        // 1️⃣ Générer matricule
        String matricule = generateMatricule();
        user.setMatricule(matricule);

        // 2️⃣ Générer mot de passe aléatoire
        String plainPassword = generateRandomPassword(8);

        // 3️⃣ Encoder mot de passe
        user.setMotDePasse(passwordEncoder.encode(plainPassword));

        // 4️⃣ Activer compte
        user.setActif(true);

        repository.save(user);

        // 5️⃣ Envoyer email
        emailService.sendActivationEmail(
                user.getEmail(),
                matricule,
                plainPassword
        );

        return user;
    }

    // 🔐 Génération matricule unique
    private String generateMatricule() {

        String prefix = "PRISM-FP/";
        String suffix = "-MG";

        int nextNumber = 1;

        // Récupérer dernier utilisateur avec matricule
        var lastUser = repository.findTopByMatriculeIsNotNullOrderByIdDesc();

        if (lastUser.isPresent()) {
            String lastMatricule = lastUser.get().getMatricule();
            // Exemple : PRISM-FP/001-MG

            try {
                String numberPart = lastMatricule
                        .replace(prefix, "")
                        .replace(suffix, "");

                int lastNumber = Integer.parseInt(numberPart);
                nextNumber = lastNumber + 1;

            } catch (Exception e) {
                nextNumber = 1;
            }
        }

        return prefix + String.format("%03d", nextNumber) + suffix;
    }


    // 🔐 Génération mot de passe sécurisé
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public AuthResponse loginWithMatricule(String matricule, String motDePasse) {

        Utilisateur user = repository.findByMatricule(matricule)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Matricule ou mot de passe incorrect"));

        if (!Boolean.TRUE.equals(user.getActif())) {
            throw new RuntimeException("Compte non activé");
        }

        if (user.getMotDePasse() == null ||
                !passwordEncoder.matches(motDePasse, user.getMotDePasse())) {

            throw new InvalidCredentialsException("Matricule ou mot de passe incorrect");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getNom(), user.getEmail(), user.getPhotoProfil());
    }

}
