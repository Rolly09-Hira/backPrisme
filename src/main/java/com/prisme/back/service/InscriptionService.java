package com.prisme.back.service;

import com.prisme.back.dto.*;
import com.prisme.back.entity.Inscription;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.entity.Formation;
import com.prisme.back.entity.Paiement;
import com.prisme.back.repository.InscriptionRepository;
import com.prisme.back.repository.UtilisateurRepository;
import com.prisme.back.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FormationRepository formationRepository;

    // Créer une inscription
    public InscriptionDTO createInscription(InscriptionRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Formation formation = formationRepository.findById(request.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        // Vérifier si l'utilisateur est déjà inscrit
        if (inscriptionRepository.existsByUtilisateurIdAndFormationId(
                request.getUtilisateurId(), request.getFormationId())) {
            throw new RuntimeException("L'utilisateur est déjà inscrit à cette formation");
        }

        Inscription inscription = new Inscription();
        inscription.setUtilisateur(utilisateur);
        inscription.setFormation(formation);
        inscription.setCommentaire(request.getCommentaire());

        Inscription savedInscription = inscriptionRepository.save(inscription);
        return mapToDTO(savedInscription);
    }

    // Mettre à jour la progression
    public InscriptionDTO updateProgression(Long inscriptionId, ProgressionUpdateRequest request) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        inscription.setProgression(request.getProgression());

        if (request.getStatut() != null) {
            inscription.setStatut(request.getStatut());
        }

        // Si progression = 100, marquer comme terminée
        if (request.getProgression() != null && request.getProgression() == 100) {
            inscription.setDateCompletion(LocalDateTime.now());
            inscription.setStatut("TERMINEE");
        }

        Inscription updatedInscription = inscriptionRepository.save(inscription);
        return mapToDTO(updatedInscription);
    }

    // Valider le certificat
    public InscriptionDTO validateCertificate(Long inscriptionId, CertificationRequest request) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        inscription.setCertificatObtenu(request.getCertificatObtenu());
        inscription.setNote(request.getNote());
        inscription.setCommentaire(request.getCommentaire());

        if (Boolean.TRUE.equals(request.getCertificatObtenu())) {
            inscription.setStatut("CERTIFIE");
        }

        Inscription validatedInscription = inscriptionRepository.save(inscription);
        return mapToDTO(validatedInscription);
    }

    // Obtenir les inscriptions d'un utilisateur
    public List<InscriptionDTO> getInscriptionsByUtilisateur(Long utilisateurId) {
        return inscriptionRepository.findByUtilisateurId(utilisateurId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Obtenir les inscriptions d'une formation
    public List<InscriptionDTO> getInscriptionsByFormation(Long formationId) {
        return inscriptionRepository.findByFormationId(formationId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Obtenir une inscription spécifique
    public InscriptionDTO getInscription(Long utilisateurId, Long formationId) {
        Inscription inscription = inscriptionRepository
                .findByUtilisateurIdAndFormationId(utilisateurId, formationId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        return mapToDTO(inscription);
    }

    // Annuler une inscription
    @Transactional
    public void cancelInscription(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        inscription.setStatut("ANNULEE");
        inscriptionRepository.save(inscription);
    }

    // Statistiques
    public Long countInscriptionsByFormation(Long formationId) {
        return inscriptionRepository.countByFormationId(formationId);
    }

    // Mapping
    private InscriptionDTO mapToDTO(Inscription inscription) {
        InscriptionDTO dto = new InscriptionDTO();
        dto.setId(inscription.getId());
        dto.setDateInscription(inscription.getDateInscription());
        dto.setStatut(inscription.getStatut());
        dto.setProgression(inscription.getProgression());
        dto.setDateDebut(inscription.getDateDebut());
        dto.setDateFin(inscription.getDateFin());
        dto.setDateCompletion(inscription.getDateCompletion());
        dto.setCertificatObtenu(inscription.getCertificatObtenu());
        dto.setNote(inscription.getNote());
        dto.setCommentaire(inscription.getCommentaire());

        if (inscription.getUtilisateur() != null) {
            dto.setUtilisateurId(inscription.getUtilisateur().getId());
            dto.setUtilisateurNom(inscription.getUtilisateur().getNom() + " " +
                    inscription.getUtilisateur().getPrenom());
            dto.setUtilisateurEmail(inscription.getUtilisateur().getEmail());
        }

        if (inscription.getFormation() != null) {
            dto.setFormationId(inscription.getFormation().getId());
            dto.setFormationTitre(inscription.getFormation().getTitre());
            dto.setFormationDescription(inscription.getFormation().getDescription());
            dto.setFormationPrix(inscription.getFormation().getPrix());
        }

        // Mapper les paiements si nécessaire
        if (inscription.getPaiements() != null && !inscription.getPaiements().isEmpty()) {
            List<PaiementDTO> paiementDTOs = inscription.getPaiements().stream()
                    .map(this::mapPaiementToDTO)
                    .collect(Collectors.toList());
            dto.setPaiements(paiementDTOs);
        }

        return dto;
    }

    private PaiementDTO mapPaiementToDTO(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setReference(paiement.getReference());
        dto.setMontant(paiement.getMontant());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setStatut(paiement.getStatut());
        dto.setMethode(paiement.getMethode());
        return dto;
    }
}