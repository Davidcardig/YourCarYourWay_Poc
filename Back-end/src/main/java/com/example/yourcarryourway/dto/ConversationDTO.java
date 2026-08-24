package com.example.yourcarryourway.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ConversationDTO {
    private UUID id;
    private UUID clientUserId;
    private UUID agentUserId;
    private String clientNom;
    private String clientPrenom;
    private String agentPrenom;
    private String sujet;
    private String statut;
    private ZonedDateTime dateOuverture;

    public ConversationDTO() {}

    public ConversationDTO(
            UUID id,
            UUID clientUserId,
            UUID agentUserId,
            String clientNom,
            String clientPrenom,
            String agentPrenom,
            String sujet,
            String statut,
            ZonedDateTime dateOuverture
    ) {
        this.id = id;
        this.clientUserId = clientUserId;
        this.agentUserId = agentUserId;
        this.clientNom = clientNom;
        this.clientPrenom = clientPrenom;
        this.agentPrenom = agentPrenom;
        this.sujet = sujet;
        this.statut = statut;
        this.dateOuverture = dateOuverture;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(UUID clientUserId) {
        this.clientUserId = clientUserId;
    }

    public UUID getAgentUserId() {
        return agentUserId;
    }

    public void setAgentUserId(UUID agentUserId) {
        this.agentUserId = agentUserId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getClientPrenom() {
        return clientPrenom;
    }

    public void setClientPrenom(String clientPrenom) {
        this.clientPrenom = clientPrenom;
    }

    public String getAgentPrenom() {
        return agentPrenom;
    }

    public void setAgentPrenom(String agentPrenom) {
        this.agentPrenom = agentPrenom;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
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
