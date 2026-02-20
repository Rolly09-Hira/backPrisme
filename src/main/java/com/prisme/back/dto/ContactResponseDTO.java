package com.prisme.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactResponseDTO {

    @NotBlank(message = "La réponse est requise")
    @Size(min = 10, max = 5000, message = "La réponse doit contenir entre 10 et 5000 caractères")
    private String reponse;
}