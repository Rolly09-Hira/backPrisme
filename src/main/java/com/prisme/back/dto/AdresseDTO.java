package com.prisme.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdresseDTO {

    private Long id;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private String complement;
}
