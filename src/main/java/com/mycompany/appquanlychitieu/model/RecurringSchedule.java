/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
a
/**
 *
 * @author Duck
 */
enum CycleType{
        DAILY,
        WEEKLY,
        MONTHLY, 
        YEARLY
};    
public class RecurringSchedule {
    private Account account;
    private Category category;
    private BigDecimal amount;
    private CycleType cycle;
    private LocalDate startDate;
    private int paidTerms;
    private LocalTime reminderTime;
    private boolean autoCreate;
    private LocalDate endDate;
    private int totalTerms;

    public RecurringSchedule(Account account, Category category, BigDecimal amount, CycleType cycle, LocalDate startDate, int paidTerms, LocalTime reminderTime, boolean autoCreate, LocalDate endDate, int totalTerms) {
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.cycle = cycle;
        this.startDate = startDate;
        this.paidTerms = paidTerms;
        this.reminderTime = reminderTime;
        this.autoCreate = autoCreate;
        this.endDate = endDate;
        this.totalTerms = totalTerms;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CycleType getCycle() {
        return cycle;
    }

    public void setCycle(CycleType cycle) {
        this.cycle = cycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getPaidTerms() {
        return paidTerms;
    }

    public void setPaidTerms(int paidTerms) {
        this.paidTerms = paidTerms;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
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

    public void setTotalTerms(int totalTerms) {
        this.totalTerms = totalTerms;
    }
    
    public NormalTransaction generateTxn(){
    }
}
