package com.prisme.back.dto;

import com.prisme.back.entity.CategorieSujet;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMessageDTO {

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    private String telephone;

    @NotBlank(message = "Le sujet est requis")
    @Size(min = 3, max = 200, message = "Le sujet doit contenir entre 3 et 200 caractères")
    private String sujet;

    @NotBlank(message = "Le message est requis")
    @Size(min = 10, max = 5000, message = "Le message doit contenir entre 10 et 5000 caractères")
    private String message;

    private CategorieSujet categorie;
}