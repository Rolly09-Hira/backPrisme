package com.prisme.back.dto;

import lombok.Data;

@Data
public class AdresseRequest {

    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private String complement;
    private Long utilisateurId;
}
