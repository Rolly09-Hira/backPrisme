package com.prisme.back.controller;

import com.prisme.back.dto.InscriptionDTO;
import com.prisme.back.dto.InscriptionRequest;
import com.prisme.back.dto.ProgressionUpdateRequest;
import com.prisme.back.dto.CertificationRequest;
import com.prisme.back.service.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;

    // Créer une inscription
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InscriptionDTO> createInscription(
            @Valid @RequestBody InscriptionRequest request) {
        InscriptionDTO created = inscriptionService.createInscription(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Mettre à jour la progression
    @PatchMapping("/{id}/progression")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InscriptionDTO> updateProgression(
            @PathVariable Long id,
            @Valid @RequestBody ProgressionUpdateRequest request) {
        InscriptionDTO updated = inscriptionService.updateProgression(id, request);
        return ResponseEntity.ok(updated);
    }

    // Valider le certificat
    @PatchMapping("/{id}/certificat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InscriptionDTO> validateCertificate(
            @PathVariable Long id,
            @Valid @RequestBody CertificationRequest request) {
        InscriptionDTO validated = inscriptionService.validateCertificate(id, request);
        return ResponseEntity.ok(validated);
    }

    // Obtenir les inscriptions d'un utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<InscriptionDTO>> getInscriptionsByUtilisateur(
            @PathVariable Long utilisateurId) {
        List<InscriptionDTO> inscriptions =
                inscriptionService.getInscriptionsByUtilisateur(utilisateurId);
        return ResponseEntity.ok(inscriptions);
    }

    // Obtenir les inscriptions d'une formation
    @GetMapping("/formation/{formationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InscriptionDTO>> getInscriptionsByFormation(
            @PathVariable Long formationId) {
        List<InscriptionDTO> inscriptions =
                inscriptionService.getInscriptionsByFormation(formationId);
        return ResponseEntity.ok(inscriptions);
    }

    // Obtenir une inscription spécifique (utilisateur + formation)
    @GetMapping("/check")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InscriptionDTO> getInscription(
            @RequestParam Long utilisateurId,
            @RequestParam Long formationId) {
        InscriptionDTO inscription =
                inscriptionService.getInscription(utilisateurId, formationId);
        return ResponseEntity.ok(inscription);
    }

    // Annuler une inscription
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelInscription(@PathVariable Long id) {
        inscriptionService.cancelInscription(id);
        return ResponseEntity.noContent().build();
    }

    // Statistiques : nombre d'inscriptions pour une formation
    @GetMapping("/formation/{formationId}/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countByFormation(@PathVariable Long formationId) {
        Long count = inscriptionService.countInscriptionsByFormation(formationId);
        return ResponseEntity.ok(count);
    }
}