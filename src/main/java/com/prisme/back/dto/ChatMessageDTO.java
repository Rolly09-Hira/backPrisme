package com.prisme.back.dto;

import com.prisme.back.entity.TypeMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatMessageDTO {

    @NotNull(message = "L'ID de la conversation est requis")
    private Long conversationId;

    @NotBlank(message = "Le contenu du message est requis")
    @Size(min = 1, max = 5000, message = "Le message doit contenir entre 1 et 5000 caractères")
    private String contenu;

    @NotNull(message = "L'ID de l'expéditeur est requis")
    private Long expediteurId;

    @NotNull(message = "L'ID du destinataire est requis")
    private Long destinataireId;

    private TypeMessage type = TypeMessage.TEXTE;

    private String pieceJointe;
}