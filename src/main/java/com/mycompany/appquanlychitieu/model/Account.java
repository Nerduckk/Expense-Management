package com.mycompany.appquanlychitieu.model;
/**
 *
 * @author Duck
 */
import java.math.BigDecimal;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private String name;
    private BigDecimal balance;
    private String currency; 

    public Account(Long id, String name, BigDecimal initialBalance, String currency) {
        this.id = id;
        setName(name);
        setBalance(initialBalance);
        setCurrency(currency);
    }
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tài khoản không được để trống.");
        }
        this.name = name.trim();
    }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) {
        this.balance = (balance == null) ? BigDecimal.ZERO : balance;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) {
        if (currency == null || !currency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Mã tiền tệ không hợp lệ (Phải là 3 ký tự in hoa).");
        }
        this.currency = currency;
    }
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền nạp phải dương.");
        }
        this.balance = this.balance.add(amount);
    }
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền rút phải dương.");
        }
        this.balance = this.balance.subtract(amount);
    }
}
