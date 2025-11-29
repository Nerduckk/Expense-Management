/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author 
 */

public class DebtTransaction extends NormalTransaction {
    private Debt debt;

    public DebtTransaction(Long id, BigDecimal amount, LocalDate date, Account account, Category category, Debt debt) {
        super(id, amount, date, account, category);
        this.debt = debt;
    }

    @Override
    public boolean isIncome() {
        if (debt == null) return super.isIncome();
        if (debt.getType() == DebtType.LENDING) return true; 
        if (debt.getType() == DebtType.BORROWING && category.getType() == CategoryType.INCOME) return true;
        return false;
    }

    @Override
    public boolean isExpense() {
        if (debt == null) return super.isExpense();
        if (debt.getType() == DebtType.BORROWING) return true;
        if (debt.getType() == DebtType.LENDING && category.getType() == CategoryType.EXPENSE) return true;
        return false;
    }

    public Debt getDebt() { return debt; }
    public void setDebt(Debt debt) { this.debt = debt; }
}
