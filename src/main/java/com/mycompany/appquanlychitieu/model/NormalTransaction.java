/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;
/**
 *
 * @author DAT
 */
public class NormalTransaction extends AbstractTransaction{
    private Account account;
    private Category category;

    public NormalTransaction() {
        super(null, BigDecimal.ZERO, LocalDate.now()); 
    }
    
    public NormalTransaction(Long id, BigDecimal amount, LocalDate date, Account account, Category category) {
        super(id, amount, date); 
        this.account = account;
        this.category = category;
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

    @Override
    public boolean isIncome() {
        return category != null && category.getType() == CategoryType.INCOME;
    }

    @Override
    public boolean isExpense() {
        return category != null && category.getType() == CategoryType.EXPENSE;
    }

    @Override
    public Account getSourceAccount() {
        return account;
    }
}
