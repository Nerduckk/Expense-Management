package com.mycompany.appquanlychitieu.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class RecurringSchedule extends BaseEntity {

    private Account account;
    private Category category;
    private BigDecimal amount;
    private CycleType cycle;
    private LocalDate startDate;
    private Integer paidTerms;      // số kỳ đã tạo giao dịch
    private Integer totalTerms;     // tổng số kỳ (có thể null = vô hạn)
    private LocalTime reminderTime;
    private boolean autoCreate;     // có tự tạo giao dịch không
    private LocalDate endDate;      // ngày kết thúc (có thể null)

    // Constructor rỗng – dùng cho DataStore hoặc tạo rồi set dần
    public RecurringSchedule() {
        super(null, "Recurring");
        this.paidTerms = 0;
        this.autoCreate = true;
    }

    public RecurringSchedule(
            Long id,
            String name,
            Account account,
            Category category,
            BigDecimal amount,
            CycleType cycle,
            LocalDate startDate,
            Integer totalTerms,
            LocalTime reminderTime,
            boolean autoCreate,
            LocalDate endDate
    ) {
        super(id, name);
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.cycle = cycle;
        this.startDate = startDate;
        this.paidTerms = 0;
        this.totalTerms = totalTerms;
        this.reminderTime = reminderTime;
        this.autoCreate = autoCreate;
        this.endDate = endDate;
    }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public CycleType getCycle() { return cycle; }
    public void setCycle(CycleType cycle) { this.cycle = cycle; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Integer getPaidTerms() { return paidTerms; }
    public void setPaidTerms(Integer paidTerms) { this.paidTerms = paidTerms; }

    public Integer getTotalTerms() { return totalTerms; }
    public void setTotalTerms(Integer totalTerms) { this.totalTerms = totalTerms; }

    public LocalTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalTime reminderTime) { this.reminderTime = reminderTime; }

    public boolean isAutoCreate() { return autoCreate; }
    public void setAutoCreate(boolean autoCreate) { this.autoCreate = autoCreate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    // ===== LOGIC HỖ TRỢ =====

    /** Lịch còn hiệu lực hay không tại 1 ngày bất kỳ */
    public boolean isActiveOn(LocalDate date) {
        if (date == null) return false;
        if (startDate != null && date.isBefore(startDate)) return false;
        if (endDate != null && date.isAfter(endDate)) return false;

        // nếu có giới hạn số kỳ
        if (totalTerms != null && paidTerms != null && paidTerms >= totalTerms) {
            return false;
        }
        return true;
    }

    /** Tính ngày đến hạn tiếp theo dựa trên startDate + paidTerms */
    public LocalDate getNextDueDate() {
        if (startDate == null) return null;
        int termIndex = (paidTerms == null) ? 0 : paidTerms; 
        LocalDate next = startDate;

        switch (cycle) {
            case WEEKLY:
                next = startDate.plusWeeks(termIndex);
                break;
            case MONTHLY:
                next = startDate.plusMonths(termIndex);
                break;
            case YEARLY:
                next = startDate.plusYears(termIndex);
                break;
            default:
                break;
        }
        return next;
    }

    /** Kiểm tra hôm nay có phải kỳ cần tạo giao dịch không */
    public boolean isDueOn(LocalDate date) {
        if (!isActiveOn(date)) return false;
        LocalDate next = getNextDueDate();
        return next != null && next.equals(date);
    }

    /**
     * Tự tạo giao dịch nếu đến ngày, trả về NormalTransaction
     * (chưa add vào TransactionService, caller sẽ xử lý tiếp).
     */
    public NormalTransaction createTransactionIfDue(LocalDate today) {
        if (!autoCreate) return null;
        if (!isDueOn(today)) return null;

        // đã đủ số kỳ?
        if (totalTerms != null && paidTerms != null && paidTerms >= totalTerms) {
            System.out.println("Đã hoàn thành số kỳ định trước.");
            return null;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return null;
        if (account == null || category == null) return null;

        NormalTransaction newTransaction = new NormalTransaction();
        // Set ID tạm thời – khi add vào TransactionService có thể đổi lại
        newTransaction.id = System.currentTimeMillis();
        newTransaction.setAccount(this.account);
        newTransaction.setCategory(this.category);
        newTransaction.setAmount(this.amount);
        newTransaction.setDate(today != null ? today : LocalDate.now());
        String baseName = (getName() != null && !getName().isBlank())
                ? getName()
                : "Auto-Txn";
        newTransaction.setName(baseName + " - " + newTransaction.getDate());

        // tăng số kỳ đã tạo
        if (this.paidTerms == null) this.paidTerms = 0;
        this.paidTerms++;

        return newTransaction;
    }
}
