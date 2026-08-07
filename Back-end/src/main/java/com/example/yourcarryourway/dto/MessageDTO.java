package com.example.yourcarryourway.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class MessageDTO {
    private UUID id;
    private UUID conversationId;
    private String expediteur;
    private String contenu;
    private ZonedDateTime horodatage;

    public MessageDTO() {}

    public MessageDTO(UUID id, UUID conversationId, String expediteur, String contenu, ZonedDateTime horodatage) {
        this.id = id;
        this.conversationId = conversationId;
        this.expediteur = expediteur;
        this.contenu = contenu;
        this.horodatage = horodatage;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public ZonedDateTime getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(ZonedDateTime horodatage) {
        this.horodatage = horodatage;
    }
}
