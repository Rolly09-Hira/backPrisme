package com.prisme.back.dto;
import lombok.Data;

@Data
public class MatiereDTO {
    private Long id;
    private String nom;
    private String description;
    private Integer ordre;
    private Long formationId;

    private FormationDTO formation;
}
