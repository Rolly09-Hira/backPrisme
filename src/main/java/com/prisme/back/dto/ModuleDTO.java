package com.prisme.back.dto;

import lombok.Data;

@Data
public class ModuleDTO {
    private Long id;
    private String titre;
    private String description;
    private Integer dureeEstimee;
    private Integer ordre;
    private Long matiereId;

    private MatiereDTO matiere;
}
