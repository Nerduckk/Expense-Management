/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.oldmodel;
import java.time.LocalDate;
import java.math.BigDecimal;
/**
 *
 * @author Duck
 */
enum CycleType{
    WEEKLY,
    MONTHLY, 
    YEARLY
}
public class Subscription {
    private long id, userId, accountId, categoryId;
    private String name, note;
    private BigDecimal amount;
    private CycleType cycle;
    private LocalDate nextDue;
    
    public Subscription(long id, String name, CycleType cycle, BigDecimal amount, LocalDate firstDue, long accountId, long categoryId, String note){
        this.id = id;
        this.name = name;
        this.cycle = cycle;
        this.amount = amount;
        this.nextDue = firstDue;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if(id > 0)
            this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        if(userId > 0)
            this.userId = userId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        if(accountId > 0)
            this.accountId = accountId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        if(categoryId > 0)
            this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!name.equals(""))
            this.name = name;
    }

    public CycleType getCycle() {
        return cycle;
    }

    public void setCycle(CycleType cycle) {
        if(cycle != null)
            this.cycle = cycle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if(!note.equals(""))
            this.note = note;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if(!amount.equals(BigDecimal.ZERO))
            this.amount = amount;
    }

    public LocalDate getNextDue() {
        return nextDue;
    }

    public void setNextDue(LocalDate nextDue) {
        if(nextDue != null && !nextDue.isBefore(LocalDate.now()))
            this.nextDue = nextDue;
        else{
            System.out.println("Ngay nhap vao khong hop le!");
            }
    }
    
    
    public Transaction markPaid(LocalDate ngay){
        String txnNote = String.format("Thanh toan %s (%s) - Ky han: %s", this.name, this.cycle, this.nextDue);
        
        Transaction txn = new Transaction(
                ngay,
                this.amount,
                CategoryType.EXPENSE,
                this.categoryId,
                this.accountId,
                txnNote,
                "subcription",
                null
        );
        
        LocalDate oldDate = this.nextDue;
        this.nextDue = tinhNgayTiepTheo();
        
        System.out.printf("Da thanh toan '%s'. Ky han da chuyen tu %s sang %s.\n", this.name, oldDate,this.nextDue);
        
        return txn;
    }
    
    public LocalDate tinhNgayTiepTheo(){
        if(this.nextDue == null) return LocalDate.now();
        
        switch(this.cycle){
            case WEEKLY:
                return this.nextDue.plusWeeks(1);
            case MONTHLY:
                return this.nextDue.plusMonths(1);
            case YEARLY:
                return this.nextDue.plusYears(1);
            default:
                return this.nextDue;
        }
    }
    
    public void doiSoTien(BigDecimal moi){
        System.out.printf("Cap nhau gia '%s' tu %s thanh %s\n", this.name, this.amount, moi);
        this.amount = moi;
    }
}
