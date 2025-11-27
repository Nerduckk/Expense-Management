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
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải lớn hơn 0.");
        }
        this.amount = amount;
    }
    public void setDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Ngày giao dịch không được null.");
        this.date = date;
    }
    public BigDecimal getAmount() { return amount; }
    public abstract boolean isIncome();
    public abstract boolean isExpense();
    public abstract Account getSourceAccount();
}
