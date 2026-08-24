package com.example.yourcarryourway.domain.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_user_id", nullable = false)
    private User clientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_user_id")
    private User agentUser;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "sujet")
    private String sujet;

    @Column(name = "date_ouverture", nullable = false)
    private ZonedDateTime dateOuverture;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    public Conversation() {
        this.id = UUID.randomUUID();
        this.dateOuverture = ZonedDateTime.now();
    }

    public Conversation(User clientUser, String sujet) {
        this();
        this.clientUser = clientUser;
        this.sujet = sujet;
        this.statut = "OUVERTE";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getClientUser() {
        return clientUser;
    }

    public void setClientUser(User clientUser) {
        this.clientUser = clientUser;
    }

    public User getAgentUser() {
        return agentUser;
    }

    public void setAgentUser(User agentUser) {
        this.agentUser = agentUser;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
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
