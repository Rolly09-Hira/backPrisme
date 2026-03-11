package com.prisme.back.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String nom;
    private String email;
    private String photoProfil;
}
