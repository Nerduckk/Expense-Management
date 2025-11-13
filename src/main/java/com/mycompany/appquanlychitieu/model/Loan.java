/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 *
 * @author Duck
 */
public class Loan {
    private long id;
    private String name;
    private LocalDate startDate;
    private BigDecimal principal;
    private BigDecimal rate;
    private long accountId;
    private String note;
    
    public Loan() {}
    public Loan(long id, String name, LocalDate startDate, BigDecimal principal, BigDecimal rate, long accountId, String note) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.principal = principal;
        this.rate = rate;
        this.accountId = accountId;
        this.note = note;
    }

    public Transaction ghiNhanGiaiNgan(LocalDate ngay, BigDecimal soTien) {
        long transactionId = 0L; 
        String description = "Giải ngân khoản vay ID: " + this.id;
        System.out.println("Ghi nhận giải ngân: " + soTien + " (INCOME) vào ngày " + ngay);
        
        return new Transaction(
            transactionId, 
            ngay, 
            soTien,
            CategoryType.INCOME, 
            0L, 
            this.accountId, 
            "Loan Disbursement", 
            description, 
            null
        );
    }
    
    public LoanPayment themThanhToan(BigDecimal amount, LocalDate ngay, long accountId) {
        System.out.println("Thêm thanh toán " + amount + " vào khoản vay ID " + this.id + " ngày " + ngay);
        return new LoanPayment(0L, this.id, ngay, amount, accountId, "Thanh toán");
    }

    public BigDecimal duNoConLai() {
        if (principal != null) {
            return principal.multiply(new BigDecimal("0.8")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }


    public long getId() { 
        return id; }
    
    public void setId(long id) { 
        if (id > 0)
        this.id = id; }

    public String getName() { 
        return name; }
    
    public void setName(String name) { 
        if (!name.equals(" "))
        this.name = name; }

    public LocalDate getStartDate() { 
        return startDate; }
    
    public void setStartDate(LocalDate startDate) { 
        this.startDate = startDate; }

    public BigDecimal getPrincipal() { 
        return principal; }
    
    public void setPrincipal(BigDecimal principal) { 
        this.principal = principal; }

    public BigDecimal getRate() { 
        return rate; }
    
    public void setRate(BigDecimal rate) { 
        this.rate = rate; }

    public long getAccountId() { 
        return accountId; }
    
    public void setAccountId(long accountId) { 
        if (accountId > 0)
        this.accountId = accountId; }

    public String getNote() { 
        return note; }
    
    public void setNote(String note) { 
        if (!note.equals(""))
        this.note = note; }
}

