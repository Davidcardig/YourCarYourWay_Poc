package com.example.yourcarryourway.domain.entities;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "canal", nullable = false)
    private String canal;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "date_ouverture", nullable = false)
    private ZonedDateTime dateOuverture;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    public Conversation() {
        this.id = UUID.randomUUID();
        this.dateOuverture = ZonedDateTime.now();
    }

    public Conversation(UUID clientId, String canal) {
        this();
        this.clientId = clientId;
        this.canal = canal;
        this.statut = "OUVERTE";
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
