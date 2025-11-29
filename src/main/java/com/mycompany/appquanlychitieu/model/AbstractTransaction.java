package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author Duck
 */
public abstract class AbstractTransaction extends BaseEntity {
    protected BigDecimal amount;
    protected LocalDate date;
    protected LocalTime time;
    protected String note;
    protected String imagePath;
    protected boolean excludeFromReport;
    
    public AbstractTransaction(Long id, BigDecimal amount, LocalDate date) {
        super(id, "TXN-" + id); 
        setAmount(amount);
        setDate(date);
        this.time = LocalTime.now();
        this.excludeFromReport = false;
    }
    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("So tien giao dich phai lon hon 0.");
        }
        this.amount = amount;
    }
    public void setDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Ngay giao dich khong duoc null.");
        this.date = date;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getNote() { 
        return note;
    }
        public BigDecimal getAmount() {
        return amount;
    }
    public LocalDate getDate() {
        return date;
    }
    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isExcludeFromReport() {
        return excludeFromReport;
    }

    public void setExcludeFromReport(boolean excludeFromReport) {
        this.excludeFromReport = excludeFromReport;
    }
    public abstract boolean isIncome();
    public abstract boolean isExpense();
    public abstract Account getSourceAccount();
}
