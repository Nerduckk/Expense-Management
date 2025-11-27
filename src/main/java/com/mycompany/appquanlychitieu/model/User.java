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
import java.util.ArrayList;
import java.util.List;

class Account {}
class Category {}
class Debt {}
class RecurringSchedule {}


public class User {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String email;
    private String passwordHash;
    private String avatarPath;
    private String defaultCurrency;

    private List<Account> accounts;
    private List<Category> categories;
    private List<Debt> debts;
    private List<RecurringSchedule> recurringSchedules;

    public User(Long id, String name, String email, String passwordHash, String defaultCurrency) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.defaultCurrency = defaultCurrency;
        
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.avatarPath = "default.png";

        this.accounts = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.recurringSchedules = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // --- Name ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now(); 
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    public List<RecurringSchedule> getRecurringSchedules() {
        return recurringSchedules;
    }

    public void setRecurringSchedules(List<RecurringSchedule> recurringSchedules) {
        this.recurringSchedules = recurringSchedules;
    }
}
