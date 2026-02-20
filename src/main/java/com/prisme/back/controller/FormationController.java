package com.prisme.back.controller;

import com.prisme.back.dto.FormationDTO;
import com.prisme.back.service.FormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FormationDTO> createFormation(
            @RequestPart("formation") FormationDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(formationService.createFormation(dto, imageFile));
    }

    @GetMapping
    public ResponseEntity<List<FormationDTO>> getAllFormations() {
        return ResponseEntity.ok(formationService.getAllFormations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormationDTO> getFormationById(@PathVariable Long id) {
        return ResponseEntity.ok(formationService.getFormationById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FormationDTO> updateFormation(
            @PathVariable Long id,
            @RequestPart("formation") FormationDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(formationService.updateFormation(id, dto, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
        formationService.deleteFormation(id);
        return ResponseEntity.noContent().build();
    }
}