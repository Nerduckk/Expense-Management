/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author DAT
 */
public class RecurringSchedule extends BaseEntity {
    private Account account;
    private Category category;
    private BigDecimal amount;
    private CycleType cycle;
    private LocalDate startDate;
    private Integer paidTerms;
    private LocalTime reminderTime;
    private boolean autoCreate;
    private LocalDate endDate;
    private Integer totalTerms;

    public RecurringSchedule(Long id, String name, Account account, Category category, BigDecimal amount, CycleType cycle, LocalDate startDate, int paidTerms, LocalTime reminderTime, boolean autoCreate, LocalDate endDate, int totalTerms) {
        super(id, name);
        setAccount(account);
        setCategory(category);
        setAmount(amount);
        setCycle(cycle);
        setStartDate(startDate);
        setPaidTerms(paidTerms);
        setReminderTime(reminderTime);
        this.autoCreate = autoCreate;
        this.endDate = endDate;
        this.totalTerms = totalTerms;
    }

    public RecurringSchedule(Long id, String name, Account account, Category category, BigDecimal amount, CycleType cycle) {
        super(id, name);
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.cycle = cycle;
        this.startDate = LocalDate.now();
        this.paidTerms = 0;
        this.autoCreate = true;
        this.reminderTime = LocalTime.of(9, 0); 
    }
    
    public void setAccount(Account account) { this.account = account; }
    public Account getAccount() { return account; }

    public void setCategory(Category category) { this.category = category; }
    public Category getCategory() { return category; }

    public void setAmount(BigDecimal amount) { 
        if(amount != null) this.amount = amount; 
    }
    public BigDecimal getAmount() { return amount; }

    public void setCycle(CycleType cycle) { this.cycle = cycle; }
    public CycleType getCycle() { return cycle; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getStartDate() { return startDate; }

    public void setPaidTerms(Integer paidTerms) { this.paidTerms = paidTerms; }
    public Integer getPaidTerms() { return paidTerms; }

    public void setReminderTime(LocalTime reminderTime) { this.reminderTime = reminderTime; }
    public LocalTime getReminderTime() { return reminderTime; }
        
    public boolean isAutoCreate() { return autoCreate; }
    public void setAutoCreate(boolean autoCreate) { this.autoCreate = autoCreate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getTotalTerms() { return totalTerms; }
    public void setTotalTerms(Integer totalTerms) { this.totalTerms = totalTerms; }
    
    public NormalTransaction generateTxn(){
        if(totalTerms != null && paidTerms != null && paidTerms >= totalTerms){
                System.out.println("Da hoan thanh so ky dinh truoc.");
                return null;
        }    
        NormalTransaction newTransaction = new NormalTransaction();
        newTransaction.id = System.currentTimeMillis(); 
        newTransaction.setAccount(this.account);
        newTransaction.setCategory(this.category);
        newTransaction.setAmount(this.amount);
        newTransaction.setDate(LocalDate.now()); 
        newTransaction.setName("Auto-Txn-" + LocalDate.now());
        return newTransaction;
    }
}
