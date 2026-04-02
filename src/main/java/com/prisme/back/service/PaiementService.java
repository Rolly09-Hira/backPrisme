package com.prisme.back.service;

import com.prisme.back.dto.PaiementDTO;
import com.prisme.back.dto.PaiementRequest;
import com.prisme.back.dto.PaiementValidationRequest;
import com.prisme.back.entity.Paiement;
import com.prisme.back.entity.Inscription;
import com.prisme.back.repository.PaiementRepository;
import com.prisme.back.repository.InscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final InscriptionRepository inscriptionRepository;

    // Créer un paiement
    public PaiementDTO createPaiement(PaiementRequest request) {
        Inscription inscription = inscriptionRepository.findById(request.getInscriptionId())
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        Paiement paiement = new Paiement();
        paiement.setMontant(request.getMontant());
        paiement.setMethode(request.getMethode());
        paiement.setDevise(request.getDevise() != null ? request.getDevise() : "EUR");
        paiement.setDescription(request.getDescription());
        paiement.setTransactionId(request.getTransactionId());
        paiement.setInscription(inscription);

        Paiement savedPaiement = paiementRepository.save(paiement);
        return mapToDTO(savedPaiement);
    }

    // Valider/Rejeter un paiement
    public PaiementDTO validatePaiement(Long id, PaiementValidationRequest request) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        paiement.setStatut(request.getStatut());
        paiement.setRemarques(request.getRemarques());
        paiement.setValidePar(request.getValidePar());

        if ("VALIDE".equals(request.getStatut())) {
            paiement.setDateValidation(LocalDateTime.now());
        }

        Paiement validatedPaiement = paiementRepository.save(paiement);
        return mapToDTO(validatedPaiement);
    }

    // Obtenir un paiement par référence
    public PaiementDTO getPaiementByReference(String reference) {
        Paiement paiement = paiementRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        return mapToDTO(paiement);
    }

    // Obtenir les paiements d'une inscription
    public List<PaiementDTO> getPaiementsByInscription(Long inscriptionId) {
        return paiementRepository.findByInscriptionId(inscriptionId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Obtenir les paiements par statut
    public List<PaiementDTO> getPaiementsByStatut(String statut) {
        return paiementRepository.findByStatut(statut)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Vérifier si une inscription est payée
    public boolean isInscriptionPayee(Long inscriptionId) {
        Double totalValide = paiementRepository.sumMontantValideByInscriptionId(inscriptionId);
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        return totalValide != null && totalValide >= inscription.getFormation().getPrix();
    }

    // Montant total payé pour une inscription
    public Double getMontantTotalPaye(Long inscriptionId) {
        Double total = paiementRepository.sumMontantValideByInscriptionId(inscriptionId);
        return total != null ? total : 0.0;
    }

    // Remboursement (annulation avec note)
    @Transactional
    public PaiementDTO rembourserPaiement(Long id, String validePar, String remarques) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        paiement.setStatut("REMBOURSE");
        paiement.setValidePar(validePar);
        paiement.setRemarques(remarques);
        paiement.setDateValidation(LocalDateTime.now());

        Paiement rembourse = paiementRepository.save(paiement);
        return mapToDTO(rembourse);
    }

    // Statistiques
    public List<Object[]> getMonthlyStats(Integer annee) {
        return paiementRepository.getMonthlyStats(annee);
    }

    // Mapping
    private PaiementDTO mapToDTO(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setReference(paiement.getReference());
        dto.setMontant(paiement.getMontant());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setStatut(paiement.getStatut());
        dto.setMethode(paiement.getMethode());
        dto.setTransactionId(paiement.getTransactionId());
        dto.setDevise(paiement.getDevise());
        dto.setDescription(paiement.getDescription());
        dto.setDerniereModification(paiement.getDerniereModification());
        dto.setDateValidation(paiement.getDateValidation());
        dto.setValidePar(paiement.getValidePar());
        dto.setRemarques(paiement.getRemarques());

        if (paiement.getInscription() != null) {
            dto.setInscriptionId(paiement.getInscription().getId());

            if (paiement.getInscription().getUtilisateur() != null) {
                dto.setUtilisateurNom(paiement.getInscription().getUtilisateur().getNom() + " " +
                        paiement.getInscription().getUtilisateur().getPrenom());
            }

            if (paiement.getInscription().getFormation() != null) {
                dto.setFormationTitre(paiement.getInscription().getFormation().getTitre());
            }
        }

        return dto;
    }
}