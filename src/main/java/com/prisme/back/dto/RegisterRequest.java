package com.prisme.back.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private String telephone;
    private LocalDate dateNaissance;
}
