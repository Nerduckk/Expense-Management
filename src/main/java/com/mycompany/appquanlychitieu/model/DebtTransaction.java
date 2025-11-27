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
    private Debt debt;

    public DebtTransaction(BigDecimal amount, LocalDate date, Account account, Category category, Debt debt) {
        super(amount, date, account, category);
        this.debt = debt;
    }

    public Debt getDebt() {
        return debt;
    }

    public void setDebt(Debt debt) {
        this.debt = debt;
    }
}
