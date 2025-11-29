/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

/**
 *
 * @author tien
 */

import java.time.LocalDateTime;

public class User extends BaseEntity {

    private String email;
    private String passwordHash;
    private String avatarPath;
    private String defaultCurrency;
    
    
    public User() {
        super();
    }

    public User(Long id, String name, String email, String defaultCurrency) {
        super(id, name);
        this.email = email;
        this.defaultCurrency = defaultCurrency;
        this.passwordHash = ""; 
        this.avatarPath = "default_avatar.png";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.setUpdatedAt(LocalDateTime.now());
    }

    public String getSummary() {
        return "User: " + getName() + " (" + email + ")";
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + email + '\'' +
                ", defaultCurrency='" + defaultCurrency + '\'' +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
