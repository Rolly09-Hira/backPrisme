package com.prisme.back.dto;

import lombok.Data;

@Data
public class LoginMatriculeRequest {
    private String matricule;
    private String motDePasse;
}
