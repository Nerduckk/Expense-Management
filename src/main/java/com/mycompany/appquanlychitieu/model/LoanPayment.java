package com.mycompany.appquanlychitieu.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanPayment {
    private long id;
    private long loanId; 
    private LocalDate payDate;
    private BigDecimal amount;
    private long accountId; 
    private String note;

    public LoanPayment() {}

    public LoanPayment(long id, long loanId, LocalDate payDate, BigDecimal amount, long accountId, String note) {
        this.setId(id);
        this.setLoanId(loanId);
        this.setPayDate(payDate);
        this.setAmount(amount);
        this.setAccountId(accountId);
        this.setNote(note);
    }

    public Transaction ghiNhanThanhToan() {
        long transactionId = 0L;
        String description = "Thanh toán khoản vay ID: " + this.loanId;
        System.out.println("Ghi nhận thanh toán: " + this.amount + " (EXPENSE) vào ngày " + this.payDate + " từ Account ID: " + this.accountId);

        return new Transaction(
            transactionId,
            this.payDate,
            this.amount,
            CategoryType.EXPENSE,
            0L,
            this.accountId,
            "Loan Payment",
            description,
            null
        );
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id >= 0)
        this.id = id;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        if (loanId > 0)
        this.loanId = loanId;
    }

    public LocalDate getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0)
        this.amount = amount;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        if (accountId > 0)
        this.accountId = accountId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (note != null)
        this.note = note;
    }
}
