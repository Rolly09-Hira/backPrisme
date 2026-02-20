package com.prisme.back.dto;

import lombok.Data;

@Data
public class FichierDTO {
    private Long id;
    private String nom;
    private String type;
    private String url;
    private Long taille;
    private String format;
    private String description;

    private Long moduleId;
    private Long formationId;

    private ModuleDTO module;
    private FormationDTO formation;
}