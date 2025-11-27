package com.mycompany.appquanlychitieu.model;
/**
 *
 * @author Duck
 */
import java.math.BigDecimal;

public class Account {
    private Long id;      
    private String name;   
    private BigDecimal balance;
    private String currency;

    public Account(Long id, String name, BigDecimal initialBalance) {
        this.id = id;
        this.name = name;
        this.balance = initialBalance;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public Long getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public String getName() { return name; }
}
