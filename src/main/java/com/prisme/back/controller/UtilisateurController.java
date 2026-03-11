package com.prisme.back.controller;

import com.prisme.back.dto.UtilisateurDTO;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;

    // Afficher tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    // Modifier un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUser(
            @PathVariable Long id,
            @RequestBody Utilisateur user) {

        return ResponseEntity.ok(service.updateUser(id, user));
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        service.activateUser(id);
        return ResponseEntity.ok("Compte activé et email envoyé");
    }
}
