/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Duck
 */
public class TransferTransaction extends AbstractTransaction {
    private Account fromAccount;
    private Account toAccount;
    private BigDecimal transferFee;

    public TransferTransaction(Long id, BigDecimal amount, LocalDate date, Account from, Account to) {
        super(id, amount, date);
        setAccounts(from, to);
        this.transferFee = BigDecimal.ZERO;
    }
    public void setAccounts(Account from, Account to) {
        if (from == null || to == null) throw new IllegalArgumentException("Tài khoản nguồn/đích không được null.");
        if (from.getId().equals(to.getId())) throw new IllegalArgumentException("Không thể chuyển khoản cho chính mình.");
        this.fromAccount = from;
        this.toAccount = to;
    }
    public void setTransferFee(BigDecimal transferFee) {
        if (transferFee != null && transferFee.compareTo(BigDecimal.ZERO) < 0) {
             throw new IllegalArgumentException("Phí chuyển khoản không được âm.");
        }
        this.transferFee = (transferFee == null) ? BigDecimal.ZERO : transferFee;
    }
    @Override
    public boolean isIncome() { return false; }
    @Override
    public boolean isExpense() { 
        return transferFee.compareTo(BigDecimal.ZERO) > 0; 
    }
    @Override
    public Account getSourceAccount() { return this.fromAccount; }
}
}
