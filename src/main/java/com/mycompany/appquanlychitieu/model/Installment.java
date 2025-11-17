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
enum CategoryType{
    INCOME,
    EXPENSE
}
public class Installment {
    private long id,accountId, categoryId;
    private int terms, paidTerms;
    private String item, status, note;
    private BigDecimal total, amountPerTerm;
    private LocalDate startDate;

    public Installment(long id, String item, BigDecimal total, int terms, 
                       LocalDate startDate, BigDecimal amountPerTerm, 
                       long accountId, long categoryId, String note) {
        this.id = id;
        this.item = item;
        this.total = total;
        this.terms = terms;
        this.startDate = startDate;
        this.amountPerTerm = amountPerTerm;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.note = note;
        this.paidTerms = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getTerms() {
        return terms;
    }

    public void setTerms(int terms) {
        this.terms = terms;
    }

    public int getPaidTerms() {
        return paidTerms;
    }

    public void setPaidTerms(int paidTerms) {
        this.paidTerms = paidTerms;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAmountPerTerm() {
        return amountPerTerm;
    }

    public void setAmountPerTerm(BigDecimal amountPerTerm) {
        this.amountPerTerm = amountPerTerm;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    
    public Transaction payTerm(LocalDate ngay){
        if(hoanTat()){
            System.out.println("Khoan tra gop da duoc thanh toan du!");
            return null;
        }
        this.paidTerms++;
        
        String transactionNote = String.format("Tra gop ky %d/%d cho %s", this.paidTerms, this.terms, this.item);
        
        Transaction newTxn = new Transaction(
                ngay,
                this.amountPerTerm,
                CategoryType.EXPENSE,
                this.categoryId,
                this.accountId,
                transactionNote,
                null,
                null
        );
        System.out.println("Da tao diao dich: " + newTxn);
        return newTxn;
    }
    
    public int conLaiKy(){
        return this.terms - this.paidTerms;
    }
    
    public boolean hoanTat(){
        return this.paidTerms >= this.terms;
    }
    
}
