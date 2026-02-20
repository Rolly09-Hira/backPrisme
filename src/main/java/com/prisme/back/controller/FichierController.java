package com.prisme.back.controller;

import com.prisme.back.dto.FichierDTO;
import com.prisme.back.service.FichierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fichiers")
@RequiredArgsConstructor
public class FichierController {

    private final FichierService fichierService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FichierDTO> uploadFichier(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "moduleId", required = false) Long moduleId,
            @RequestParam(value = "formationId", required = false) Long formationId,
            @RequestParam(value = "matiereId", required = false) Long matiereId,
            @RequestParam(value = "description", required = false) String description) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fichierService.uploadAndCreateFichier(file, moduleId, matiereId, formationId, description));
    }

    @GetMapping
    public ResponseEntity<List<FichierDTO>> getAllFichiers() {
        return ResponseEntity.ok(fichierService.getAllFichiers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichierDTO> getFichierById(@PathVariable Long id) {
        return ResponseEntity.ok(fichierService.getFichierById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichierDTO> updateFichier(
            @PathVariable Long id,
            @RequestBody FichierDTO dto) {
        return ResponseEntity.ok(fichierService.updateFichier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFichier(@PathVariable Long id) {
        fichierService.deleteFichier(id);
        return ResponseEntity.noContent().build();
    }

    // Sous-ressources

    @GetMapping("/by-module/{moduleId}")
    public ResponseEntity<List<FichierDTO>> getFichiersByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(fichierService.getFichiersByModule(moduleId));
    }

    @GetMapping("/by-matiere/{matiereId}")
    public ResponseEntity<List<FichierDTO>> getFichiersByMatiere(@PathVariable Long matiereId) {
        return ResponseEntity.ok(fichierService.getFichiersByMatiere(matiereId));
    }

    @GetMapping("/by-formation/{formationId}")
    public ResponseEntity<List<FichierDTO>> getFichiersByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(fichierService.getFichiersByFormation(formationId));
    }
}