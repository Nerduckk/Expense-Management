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
    private String trans_id, user_id, category_id, trans_type, note;
    private BigDecimal amount;
    private LocalDate date;
    
    private static long stt = 0;
    
    public Transaction(String user_id, String category_id, BigDecimal amount, String trans_type, String note, LocalDate date ){
        this.trans_id = "GD" + String.format("%d", ++stt);
        setUser_id(user_id);
        setCategory_id(category_id);
        setAmount(amount);
        setTrans_type(trans_type);
        this.note = note;
        setDate(date);
    }

    public String getTrans_id() {
        return trans_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_Id) {
        if(!user_id.equals("")) 
            this.user_id = user_id;
    }

    public String getCategory_id() {
        return this.category_id;
    }

    public void setCategory_id(String category_id) {
        if(!category_id.equals(""))
            this.category_id = category_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) > 0)
            this.amount = amount;
    }

    public String getTrans_type() {
        return trans_type;
    }

public void setTrans_type(String trans_type) {
    if (trans_type.equalsIgnoreCase("Thu") || trans_type.equalsIgnoreCase("Chi")) {
        this.trans_type = trans_type;
    } else {
        System.out.println("Loại giao dịch không hợp lệ! Chỉ chấp nhận 'Thu' hoặc 'Chi'.");
    }
}
    public String getNote() {
        return note;
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
    
    public void deleteTransaction(){
        this.trans_id = "";
        this.user_id = "";
        this.category_id = "";
        this.amount = BigDecimal.ZERO;
        this.trans_type = "";
        this.note = "";
        this.date = LocalDate.now();
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + trans_id +
                ", userId=" + user_id +
                ", category_Id=" + category_id +
                ", amount=" + amount +
                ", type='" + trans_type + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}
