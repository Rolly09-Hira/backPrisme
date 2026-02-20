package com.prisme.back.controller;

import com.prisme.back.dto.*;
import com.prisme.back.entity.Utilisateur;
import com.prisme.back.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurService service;

    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(service.checkEmail(email));
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<Utilisateur> register(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            @RequestParam(required = false) String motDePasse,
            @RequestParam String role,
            @RequestParam String telephone,
            @RequestParam LocalDate dateNaissance,
            @RequestParam(required = false) MultipartFile photo
    ) {

        return ResponseEntity.ok(
                service.register(nom, prenom, email, motDePasse, role, telephone, dateNaissance, photo)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        // Appel service login
        AuthResponse response = service.login(request.getEmail(), request.getMotDePasse());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login-matricule")
    public ResponseEntity<AuthResponse> loginWithMatricule(
            @RequestBody LoginMatriculeRequest request) {

        return ResponseEntity.ok(
                service.loginWithMatricule(
                        request.getMatricule(),
                        request.getMotDePasse()
                )
        );
    }


}
