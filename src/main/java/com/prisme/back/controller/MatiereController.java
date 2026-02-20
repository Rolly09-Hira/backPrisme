package com.prisme.back.controller;

import com.prisme.back.dto.MatiereDTO;
import com.prisme.back.service.MatiereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matieres")
@RequiredArgsConstructor
public class MatiereController {

    private final MatiereService matiereService;

    @PostMapping
    public ResponseEntity<MatiereDTO> createMatiere(@RequestBody MatiereDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(matiereService.createMatiere(dto));
    }

    @GetMapping
    public ResponseEntity<List<MatiereDTO>> getAllMatieres() {
        return ResponseEntity.ok(matiereService.getAllMatieres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatiereDTO> getMatiereById(@PathVariable Long id) {
        return ResponseEntity.ok(matiereService.getMatiereById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatiereDTO> updateMatiere(
            @PathVariable Long id,
            @RequestBody MatiereDTO dto) {
        return ResponseEntity.ok(matiereService.updateMatiere(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatiere(@PathVariable Long id) {
        matiereService.deleteMatiere(id);
        return ResponseEntity.noContent().build();
    }

    // Sous-ressource : matières d'une formation
    @GetMapping("/by-formation/{formationId}")
    public ResponseEntity<List<MatiereDTO>> getMatieresByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(matiereService.getMatieresByFormation(formationId));
    }
}