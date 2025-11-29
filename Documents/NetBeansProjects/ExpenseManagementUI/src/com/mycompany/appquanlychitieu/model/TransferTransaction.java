package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferTransaction extends AbstractTransaction {
    private Account fromAccount;
    private Account toAccount;
    private BigDecimal transferFee;

    public TransferTransaction(Long id, BigDecimal amount, LocalDate date, Account from, Account to) {
        super(id, amount, date);
        setAccounts(from, to);
        this.transferFee = BigDecimal.ZERO;
    }
    
    // Thêm constructor đầy đủ nếu cần set phí ngay lúc tạo
    public TransferTransaction(Long id, BigDecimal amount, LocalDate date, Account from, Account to, BigDecimal fee) {
        super(id, amount, date);
        setAccounts(from, to);
        setTransferFee(fee);
    }

    public void setAccounts(Account from, Account to) {
        if (from == null || to == null) throw new IllegalArgumentException("Tai khoan nguon/dich khong duoc null.");
        if (from.getId().equals(to.getId())) throw new IllegalArgumentException("Khong the chuyen khoan cho chinh minh.");
        this.fromAccount = from;
        this.toAccount = to;
    }

    public void setTransferFee(BigDecimal transferFee) {
        if (transferFee != null && transferFee.compareTo(BigDecimal.ZERO) < 0) {
             throw new IllegalArgumentException("Phi chuyen khoan khong duoc am.");
        }
        this.transferFee = (transferFee == null) ? BigDecimal.ZERO : transferFee;
    }

    // --- CÁC GETTER BỊ THIẾU (CẦN THÊM VÀO) ---
    public Account getToAccount() { return toAccount; }
    public Account getFromAccount() { return fromAccount; }
    public BigDecimal getTransferFee() { return transferFee; }

    @Override
    public boolean isIncome() { return false; }
    @Override
    public boolean isExpense() { 
        return transferFee.compareTo(BigDecimal.ZERO) > 0; 
    }
    @Override
    public Account getSourceAccount() { return this.fromAccount; }
}