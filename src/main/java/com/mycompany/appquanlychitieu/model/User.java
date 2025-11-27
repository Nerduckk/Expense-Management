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


public class User extends BaseEntity {
    private String email;
    private String passwordHash;
    private String avatarPath;
    private String defaultCurrency;

    private List<Account> accounts;
    private List<Category> categories;
    private List<Debt> debts;
    private List<RecurringSchedule> recurringSchedules;

    public User(Long id, String name, String email, String passwordHash, String defaultCurrency) {
        super(id, name);
        
        this.email = email;
        this.passwordHash = passwordHash;
        this.defaultCurrency = defaultCurrency;
        this.avatarPath = "default_avatar.png"; 

     
        this.accounts = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.recurringSchedules = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
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
    
    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public String getSummary() {
        return "User Info: " + this.name + " (" + this.email + ")";
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() + // Lấy từ cha
                ", name='" + getName() + '\'' + // Lấy từ cha
                ", email='" + email + '\'' +
                ", currency='" + defaultCurrency + '\'' +
                ", accountsCount=" + accounts.size() +
                '}';
    }
}
