/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java 
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
public class Debt extends BaseEntity {
    private DebtType type;
    private String personName;
    private BigDecimal principalAmount;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate dueDate;
    private DebtStatus status;
    private List<DebtTransaction> transactions;

    public Debt() { this.transactions = new ArrayList<>(); }
    
    public Debt(Long id, String name, DebtType type, BigDecimal principalAmount, String personName) {
        super(id, name);
        this.type = type;
        this.principalAmount = principalAmount;
        this.personName = personName;
        this.status = DebtStatus.ACTIVE;
        this.startDate = LocalDate.now();
        this.transactions = new ArrayList<>();
    }

    public BigDecimal getPaidAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (transactions != null) 
            for (DebtTransaction t : transactions) 
                if (t.getAmount() != null) total = total.add(t.getAmount());
        return total;
    }

    public BigDecimal getRemainingAmount() {
        return principalAmount == null ? BigDecimal.ZERO : principalAmount.subtract(getPaidAmount());
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
    public void setTransactions(List<DebtTransaction> transactions) { this.transactions = transactions; }
}
