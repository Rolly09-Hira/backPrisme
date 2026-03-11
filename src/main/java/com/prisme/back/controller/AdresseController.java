package com.prisme.back.controller;

import com.prisme.back.dto.AdresseRequest;
import com.prisme.back.dto.AdresseResponse;
import com.prisme.back.service.AdresseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adresses")
@RequiredArgsConstructor
public class AdresseController {

    private final AdresseService service;

    // ✅ PUBLIC
    @PostMapping
    public ResponseEntity<AdresseResponse> createAdresse(
            @RequestBody AdresseRequest request) {

        return ResponseEntity.ok(service.createAdresse(request));
    }

    // 🔐 PROTECTED
    @GetMapping("/{id}")
    public ResponseEntity<AdresseResponse> getAdresse(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAdresseById(id));
    }

    // 🔐 PROTECTED
    @PutMapping("/{id}")
    public ResponseEntity<AdresseResponse> updateAdresse(
            @PathVariable Long id,
            @RequestBody AdresseRequest request) {

        return ResponseEntity.ok(service.updateAdresse(id, request));
    }

    // 🔐 PROTECTED
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdresse(@PathVariable Long id) {

        service.deleteAdresse(id);
        return ResponseEntity.ok("Adresse supprimée avec succès");
    }
}
