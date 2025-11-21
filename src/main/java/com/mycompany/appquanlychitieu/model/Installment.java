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
//enum CategoryType{
//    INCOME,
//    EXPENSE
//}
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
        if(id > 0)
            this.id = id;
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

    public int getTerms() {
        return terms;
    }

    public void setTerms(int terms) {
        if(terms > 0)
            this.terms = terms;
    }

    public int getPaidTerms() {
        return paidTerms;
    }

    public void setPaidTerms(int paidTerms) {
        if(paidTerms > 0)
            this.paidTerms = paidTerms;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        if(!item.equals(""))
            this.item = item;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if(!status.equals(""))
            this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if(!note.equals(""))
            this.note = note;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        if(!total.equals(BigDecimal.ZERO))
            this.total = total;
    }

    public BigDecimal getAmountPerTerm() {
        return amountPerTerm;
    }

    public void setAmountPerTerm(BigDecimal amountPerTerm) {
        if(!amountPerTerm.equals(BigDecimal.ZERO))
            this.amountPerTerm = amountPerTerm;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if(startDate != null && !startDate.isAfter(LocalDate.now()))
            this.startDate = startDate;
        else{
            System.out.println("Ngay nhap vao khong hop le!");
            }
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
