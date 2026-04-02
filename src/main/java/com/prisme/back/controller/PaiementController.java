package com.prisme.back.controller;

import com.prisme.back.dto.PaiementDTO;
import com.prisme.back.dto.PaiementRequest;
import com.prisme.back.dto.PaiementValidationRequest;
import com.prisme.back.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    // Créer un paiement
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> createPaiement(@Valid @RequestBody PaiementRequest request) {
        PaiementDTO created = paiementService.createPaiement(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Valider un paiement
    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> validatePaiement(
            @PathVariable Long id,
            @Valid @RequestBody PaiementValidationRequest request) {
        PaiementDTO validated = paiementService.validatePaiement(id, request);
        return ResponseEntity.ok(validated);
    }

    // Rembourser un paiement
    @PatchMapping("/{id}/rembourser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> rembourserPaiement(
            @PathVariable Long id,
            @RequestParam String validePar,
            @RequestParam(required = false) String remarques) {
        PaiementDTO rembourse = paiementService.rembourserPaiement(id, validePar, remarques);
        return ResponseEntity.ok(rembourse);
    }

    // Obtenir un paiement par référence
    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> getPaiementByReference(@PathVariable String reference) {
        PaiementDTO paiement = paiementService.getPaiementByReference(reference);
        return ResponseEntity.ok(paiement);
    }

    // Obtenir les paiements d'une inscription
    @GetMapping("/inscription/{inscriptionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByInscription(
            @PathVariable Long inscriptionId) {
        List<PaiementDTO> paiements = paiementService.getPaiementsByInscription(inscriptionId);
        return ResponseEntity.ok(paiements);
    }

    // Obtenir les paiements par statut
    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByStatut(@PathVariable String statut) {
        List<PaiementDTO> paiements = paiementService.getPaiementsByStatut(statut);
        return ResponseEntity.ok(paiements);
    }

    // Vérifier si une inscription est payée
    @GetMapping("/inscription/{inscriptionId}/payee")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> isInscriptionPayee(@PathVariable Long inscriptionId) {
        boolean payee = paiementService.isInscriptionPayee(inscriptionId);
        return ResponseEntity.ok(payee);
    }

    // Montant total payé pour une inscription
    @GetMapping("/inscription/{inscriptionId}/total")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Double> getMontantTotal(@PathVariable Long inscriptionId) {
        Double total = paiementService.getMontantTotalPaye(inscriptionId);
        return ResponseEntity.ok(total);
    }

    // Statistiques mensuelles
    @GetMapping("/statistiques/mensuelles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getMonthlyStats(@RequestParam Integer annee) {
        List<Object[]> stats = paiementService.getMonthlyStats(annee);
        return ResponseEntity.ok(stats);
    }
}