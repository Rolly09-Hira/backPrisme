package com.prisme.back.controller;

import com.prisme.back.dto.ModuleDTO;
import com.prisme.back.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(@RequestBody ModuleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduleService.createModule(dto));
    }

    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleDTO> updateModule(
            @PathVariable Long id,
            @RequestBody ModuleDTO dto) {
        return ResponseEntity.ok(moduleService.updateModule(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    // Sous-ressource : modules d'une matière
    @GetMapping("/by-matiere/{matiereId}")
    public ResponseEntity<List<ModuleDTO>> getModulesByMatiere(@PathVariable Long matiereId) {
        return ResponseEntity.ok(moduleService.getModulesByMatiere(matiereId));
    }
}