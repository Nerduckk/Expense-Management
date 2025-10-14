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
public class Transaction {
    private long Id, user_Id;
    private int category_Id;
    private BigDecimal amount;
    private String type, note;
    private LocalDate date;
    
    public Transaction(long Id, long user_Id, int category_Id, BigDecimal amount, String type, String note, LocalDate date ){
        this.Id = Id;
        this.user_Id = user_Id;
        this.category_Id = category_Id;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.date = date;
    }

    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public long getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(long user_Id) {
        this.user_Id = user_Id;
    }

    public int getCategory_Id() {
        return category_Id;
    }

    public void setCategory_Id(int category_Id) {
        this.category_Id = category_Id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public void deleteTransaction(){
        this.Id = 0;
        this.user_Id = 0;
        this.category_Id = 0;
        this.amount = BigDecimal.ZERO;
        this.type = "";
        this.note = "";
        this.date = LocalDate.now();
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + Id +
                ", userId=" + user_Id +
                ", category_Id=" + category_Id +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}
