/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
/**
 *
 * @author DarkBre
 */
enum DebtType {
    BORROWING,
    LENDING
}

enum DebtStatus {
    ACTIVE,
    COMPLETED,
    BAD_DEBT
}


abstract class BaseEntity {
    protected Long id;
    protected String name;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}


class Debt extends BaseEntity {
    private DebtType type;
    private String personName;
    private BigDecimal principalAmount;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private DebtStatus status;
    
    
    private List<DebtTransaction> transactions;

    public Debt(String name, DebtType type, String personName, BigDecimal principalAmount, LocalDate startDate) {
        this.setName(name);
        this.type = type;
        this.personName = personName;
        this.principalAmount = principalAmount;
        this.startDate = startDate;
        this.status = DebtStatus.ACTIVE;
        this.transactions = new ArrayList<>();
    }

   
    public BigDecimal getPaidAmount() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        if (transactions != null) {
            for (DebtTransaction txn : transactions) {
                totalPaid = totalPaid.add(txn.getAmount());
            }
        }
        return totalPaid;
    }

   
    public BigDecimal getRemainingAmount() {
        if (principalAmount == null) return BigDecimal.ZERO;
        return principalAmount.subtract(getPaidAmount());
    }

    
    public void addTransaction(DebtTransaction txn) {
        this.transactions.add(txn);
        checkStatus();
    }

    
    private void checkStatus() {
        if (getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            this.status = DebtStatus.COMPLETED;
        }
    }

  
    public DebtType getType() { return type; }
    public void setType(DebtType type) { this.type = type; }
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public DebtStatus getStatus() { return status; }
    public void setStatus(DebtStatus status) { this.status = status; }
    public List<DebtTransaction> getTransactions() { return transactions; }
}



abstract class AbstractTransaction extends BaseEntity {
    protected BigDecimal amount;
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

class NormalTransaction extends AbstractTransaction {
    
}

class DebtTransaction extends NormalTransaction {
    private Debt debt;

    public DebtTransaction(BigDecimal amount, Debt debt) {
        this.setAmount(amount);
        this.debt = debt;
    }
    
    public Debt getDebt() { return debt; }
}
