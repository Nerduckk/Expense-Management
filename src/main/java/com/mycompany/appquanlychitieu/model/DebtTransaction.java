/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

/**
 *
 * @author 
 */
public class DebtTransaction extends NormalTransaction {
    public Debt debt;

    public DebtTransaction(BigDecimal amount, LocalDate date, Account account, Category category, Debt debt) {
        super(amount, date, account, category);
        this.debt = debt;
    }
}
