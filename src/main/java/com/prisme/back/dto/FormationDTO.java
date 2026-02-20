package com.prisme.back.dto;

import lombok.Data;

@Data
public class FormationDTO {
    private Long id;
    private String titre;
    private String description;
    private Double prix;
    private String categorie;
    private String image;
    private String duree;
    private String langue;
}
