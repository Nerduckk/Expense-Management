/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
/**
 *
 * @author Duck
 */
enum CategoryType{
    INCOME,
    EXPENSE
}
public class Transaction {
    private long id, accountId, categoryId;
    private String tag, note, receiptPath;
    private CategoryType type;
    private BigDecimal amount;
    private LocalDate date;
    
    private static long stt = 0;
    
    public Transaction(LocalDate date, BigDecimal amount, CategoryType type,
                       long categoryId, long accountId, String note,
                       String tag, String receiptPath){
        this.id = ++stt;
        setDate(date);
        setAmount(amount);
        setType(type);
        setAccountId(accountId);
        setCategoryId(categoryId);
        setAccountId(accountId);
        setNote(note);
        setTag(tag);
        setReceiptPath(receiptPath);
    }

    public long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        if(accountId != 0) 
            this.accountId = accountId;
    }

    public long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(long categoryId) {
        if(categoryId != 0)
            this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) > 0)
            this.amount = amount;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        if(!Objects.equals(this.type, type))
            this.type = type;
    }
    
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if(!this.note.equals(note))
            this.note = note;
    }
    
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if(date != null && !date.isAfter(LocalDate.now()))
            this.date = date;
        else{
            System.out.println("Ngày nhập vào không hợp lệ!");
            }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if(!this.tag.equals(tag))
            this.tag = tag;
    }

    public String getReceiptPath() {
        return receiptPath;
    }

    public void setReceiptPath(String receiptPath) {
        if(!this.receiptPath.equals(receiptPath))
            this.receiptPath = receiptPath;
    }
    
    public boolean laThu(){
        return this.type == CategoryType.INCOME;
    }
    
    public boolean laChi(){
        return this.type == CategoryType.EXPENSE;
    }
    @Override
    public String toString() {
        return String.format("Giao dich[ID=%d, Ngay=%s, Loai=%s, So tien=%s, Note=%s]", 
                             id, date, type, amount, note);
    }
}  

