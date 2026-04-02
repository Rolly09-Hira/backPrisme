package com.prisme.back.dto;

import lombok.Data;

@Data
public class CertificationRequest {
    private Boolean certificatObtenu;
    private Double note;
    private String commentaire;
}