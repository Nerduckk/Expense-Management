/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

/**
 *
 * @author DAT
 */
public class NormalTransaction {
    private Account account;
    private Category category;
    
    public NormalTransaction(Account account, Category category) {
        setAccount(account);
        setCategory(category);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        if(account != null)
            this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if(category != null)
            this.category = category;
    }
}
