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
    public Long id;
    public String name;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    
    public String email;
    public String passwordHash;
    public String avatarPath;
    public String defaultCurrency;

    public List<Account> accounts;
    public List<Category> categories;
    public List<Debt> debts;
    public List<RecurringSchedule> recurringSchedules;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accounts = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.recurringSchedules = new ArrayList<>();
    }
}
