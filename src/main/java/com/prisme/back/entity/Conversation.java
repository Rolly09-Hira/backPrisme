package com.prisme.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "conversations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"utilisateur_id", "admin_id"})
        }
)
@Data
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Utilisateur admin;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "dernier_message")
    private LocalDateTime dernierMessage;

    @Column(name = "est_active")
    private Boolean estActive = true;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageConversation> messages = new ArrayList<>();

    // Champ transient pour le nombre de messages non lus (non persisté en base)
    @Transient
    private Long unreadCount;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dernierMessage = LocalDateTime.now();
    }
}