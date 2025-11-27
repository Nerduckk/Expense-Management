/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.oldmodel;
import java.time.LocalDate;
import java.math.BigDecimal;
/**
 *
 * @author Duc
 */
public class LendPayment {

    private long id;
    private long lendId;
    private LocalDate payDate;
    private BigDecimal amount;
    private String note;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public long getLendId() { return lendId; }
    public void setLendId(long lendId) { this.lendId = lendId; }
    
    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = (payDate != null && !payDate.isAfter(LocalDate.now())) ? payDate : LocalDate.now(); }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = amount;
        } else {
            this.amount = BigDecimal.ZERO; 
        }
    }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
