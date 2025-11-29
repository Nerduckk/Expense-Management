package com.mycompany.appquanlychitieu.model;
import java.io.Serializable; 
import java.math.BigDecimal;


public class Account extends BaseEntity {

    private BigDecimal balance;
    private String currency; 

    public Account(Long id, String name, BigDecimal initialBalance, String currency) {
        super(id, name); 
        setBalance(initialBalance);
        setCurrency(currency);
    }

  

    public BigDecimal getBalance() { return balance; }
    
    public void setBalance(BigDecimal balance) {
        this.balance = (balance == null) ? BigDecimal.ZERO : balance;
    }

    public String getCurrency() { return currency; }
    
    public void setCurrency(String currency) {
        if (currency == null || !currency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Ma tien te khong hop le (Phai la 3 ky tu in hoa).");
        }
        this.currency = currency;
    }

    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("So tien nap phai duong.");
        }
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("So tien rut phai duong.");
        }
        this.balance = this.balance.subtract(amount);
    }
}
