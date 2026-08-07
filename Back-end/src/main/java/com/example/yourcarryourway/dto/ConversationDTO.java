package com.example.yourcarryourway.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ConversationDTO {
    private UUID id;
    private UUID clientId;
    private String canal;
    private String statut;
    private ZonedDateTime dateOuverture;

    public ConversationDTO() {}

    public ConversationDTO(UUID id, UUID clientId, String canal, String statut, ZonedDateTime dateOuverture) {
        this.id = id;
        this.clientId = clientId;
        this.canal = canal;
        this.statut = statut;
        this.dateOuverture = dateOuverture;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public ZonedDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(ZonedDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }
}
