package com.prisme.back.service;

import com.prisme.back.entity.Utilisateur;
import com.prisme.back.repository.UtilisateurRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtilisateurInitializationService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@prisme.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_NOM = "Admin";
    private static final String ADMIN_PRENOM = "Super";
    private static final String ADMIN_TELEPHONE = "0123456789";
    private static final String ADMIN_MATRICULE = "ADMIN-001";

    private static final String ETUDIANT_EMAIL = "etudiant@prisme.com";
    private static final String ETUDIANT_PASSWORD = "etudiant123";
    private static final String ETUDIANT_NOM = "Dupont";
    private static final String ETUDIANT_PRENOM = "Jean";
    private static final String ETUDIANT_TELEPHONE = "0987654321";
    private static final String ETUDIANT_MATRICULE = "ETU-2024-001";

    @PostConstruct
    @Transactional
    public void initDefaultUsers() {
        log.info("🚀 Démarrage de l'initialisation des utilisateurs par défaut...");
        createAdminIfNotExists();
        createDefaultStudentIfNotExists();
        log.info("✅ Initialisation des utilisateurs par défaut terminée");
    }

    /**
     * Crée l'utilisateur admin s'il n'existe pas déjà
     */
    private void createAdminIfNotExists() {
        if (!utilisateurRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("👤 Création de l'utilisateur admin...");

            Utilisateur admin = new Utilisateur();
            admin.setNom(ADMIN_NOM);
            admin.setPrenom(ADMIN_PRENOM);
            admin.setEmail(ADMIN_EMAIL);
            admin.setMotDePasse(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole("ADMIN");
            admin.setActif(true);
            admin.setTelephone(ADMIN_TELEPHONE);
            admin.setDateNaissance(LocalDate.of(1990, 1, 1));
            admin.setMatricule(ADMIN_MATRICULE);
            admin.setDateCreation(LocalDateTime.now());

            utilisateurRepository.save(admin);
            log.info("✅ Admin créé avec succès - Email: {}, Matricule: {}", ADMIN_EMAIL, ADMIN_MATRICULE);
        } else {
            log.info("ℹ️ L'admin avec l'email {} existe déjà", ADMIN_EMAIL);
        }
    }

    /**
     * Crée l'étudiant par défaut s'il n'existe pas déjà
     */
    private void createDefaultStudentIfNotExists() {
        if (!utilisateurRepository.existsByEmail(ETUDIANT_EMAIL)) {
            log.info("👤 Création de l'étudiant par défaut...");

            Utilisateur etudiant = new Utilisateur();
            etudiant.setNom(ETUDIANT_NOM);
            etudiant.setPrenom(ETUDIANT_PRENOM);
            etudiant.setEmail(ETUDIANT_EMAIL);
            etudiant.setMotDePasse(passwordEncoder.encode(ETUDIANT_PASSWORD));
            etudiant.setRole("UTILISATEUR");
            etudiant.setActif(true);
            etudiant.setTelephone(ETUDIANT_TELEPHONE);
            etudiant.setDateNaissance(LocalDate.of(2000, 5, 15));
            etudiant.setMatricule(ETUDIANT_MATRICULE);
            etudiant.setDateCreation(LocalDateTime.now());

            utilisateurRepository.save(etudiant);
            log.info("✅ Étudiant créé avec succès - Email: {}, Matricule: {}", ETUDIANT_EMAIL, ETUDIANT_MATRICULE);
        } else {
            log.info("ℹ️ L'étudiant avec l'email {} existe déjà", ETUDIANT_EMAIL);
        }
    }

    /**
     * Méthode utilitaire pour créer un étudiant personnalisé
     * @param nom Nom de l'étudiant
     * @param prenom Prénom de l'étudiant
     * @param email Email de l'étudiant
     * @param password Mot de passe
     * @return L'étudiant créé
     */
    @Transactional
    public Utilisateur createCustomStudent(String nom, String prenom, String email, String password) {
        if (utilisateurRepository.existsByEmail(email)) {
            log.warn("⚠️ Un utilisateur avec l'email {} existe déjà", email);
            return utilisateurRepository.findByEmail(email).orElse(null);
        }

        Utilisateur etudiant = new Utilisateur();
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setEmail(email);
        etudiant.setMotDePasse(passwordEncoder.encode(password));
        etudiant.setRole("UTILISATEUR");
        etudiant.setActif(true);
        etudiant.setTelephone("0600000000");
        etudiant.setDateNaissance(LocalDate.of(2000, 1, 1));
        etudiant.setMatricule(generateMatricule());
        etudiant.setDateCreation(LocalDateTime.now());

        Utilisateur saved = utilisateurRepository.save(etudiant);
        log.info("✅ Étudiant personnalisé créé: {} {}", nom, prenom);
        return saved;
    }

    /**
     * Génère un matricule unique
     */
    private String generateMatricule() {
        String prefix = "ETU-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = utilisateurRepository.count() + 1;
        return prefix + "-" + String.format("%04d", count);
    }
}