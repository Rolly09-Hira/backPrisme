package com.prisme.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationDTO {
    private Long id;
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenom;
    private String utilisateurEmail;
    private Long adminId;
    private String adminNom;
    private String adminPrenom;
    private String adminEmail;
    private LocalDateTime dateCreation;
    private LocalDateTime dernierMessage;
    private Boolean estActive;
    private Long nombreMessagesNonLus;
    private String dernierMessageContenu;
    private LocalDateTime dernierMessageDate;
}