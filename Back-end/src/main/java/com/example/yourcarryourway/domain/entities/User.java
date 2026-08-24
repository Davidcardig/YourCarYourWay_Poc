package com.example.yourcarryourway.domain.entities;

import com.example.yourcarryourway.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private ZonedDateTime dateNaissance;

    @Column(name = "langue_creation")
    private String langueCreation;

    @Column(name = "date_creation")
    private ZonedDateTime dateCreation;

    public User() {
        this.id = UUID.randomUUID();
        this.dateCreation = ZonedDateTime.now();
        this.langueCreation = "FR";
        this.role = UserRole.CLIENT;
    }

    public User(String email, String password, String nom, String prenom, UserRole role) {
        this();
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public ZonedDateTime getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(ZonedDateTime dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLangueCreation() {
        return langueCreation;
    }

    public void setLangueCreation(String langueCreation) {
        this.langueCreation = langueCreation;
    }

    public ZonedDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(ZonedDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
