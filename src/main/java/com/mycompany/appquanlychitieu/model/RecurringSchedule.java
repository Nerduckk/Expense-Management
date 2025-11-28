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

    public RecurringSchedule(Account account, Category category, BigDecimal amount, CycleType cycle, LocalDate startDate, int paidTerms, LocalTime reminderTime, boolean autoCreate, LocalDate endDate, int totalTerms) {
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        if(account != null)
            this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if(category != null)
            this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if(!amount.equals(BigDecimal.ZERO))
            this.amount = amount;
    }

    public CycleType getCycle() {
        return cycle;
    }

    public void setCycle(CycleType cycle) {
        if(cycle != null)
            this.cycle = cycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if(startDate != null)
            this.startDate = startDate;
    }

    public Integer getPaidTerms() {
        return paidTerms;
    }

    public void setPaidTerms(Integer paidTerms) {
        if(paidTerms != null)
            this.paidTerms = paidTerms;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        if(reminderTime != null)
            this.reminderTime = reminderTime;
    }
        
    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getTotalTerms() {
        return totalTerms;
    }

    public void setTotalTerms(Integer totalTerms) {
        this.totalTerms = totalTerms;
    }
    
    public NormalTransaction generateTxn(){
        if(totalTerms != null && paidTerms >= totalTerms){
                System.out.println("Da hoan thanh so ky dinh truoc.");
                return null;
        }    
        NormalTransaction newTransaction = new NormalTransaction();

        newTransaction.setAccount(this.account);
        newTransaction.setCategory(this.category);
        newTransaction.setAmount(this.amount);
        newTransaction.setDate(LocalDate.now()); 
        newTransaction.setName("Auto-Txn-" + LocalDate.now());
        return newTransaction;
    }
}
