package com.example.yourcarryourway.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "expediteur", nullable = false)
    private String expediteur;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "horodatage", nullable = false)
    private ZonedDateTime horodatage;

    public Message() {
        this.id = UUID.randomUUID();
        this.horodatage = ZonedDateTime.now();
    }

    public Message(Conversation conversation, String expediteur, String contenu) {
        this();
        this.conversation = conversation;
        this.expediteur = expediteur;
        this.contenu = contenu;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
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
