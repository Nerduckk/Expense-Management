package com.mycompany.appquanlychitieu.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtTransaction extends NormalTransaction {
    private Debt debt;

    // SỬA: Thêm Long id vào tham số đầu tiên để khớp với Main
    public DebtTransaction(Long id, BigDecimal amount, LocalDate date, Account account, Category category, Debt debt) {
        // Truyền id lên cha
        super(id, amount, date, account, category);
        this.debt = debt;
    }

    @Override
    public boolean isIncome() {
        if (debt == null) return super.isIncome();
        if (debt.getType() == DebtType.LENDING) return true; // Thu hồi nợ -> Thu
        // Nếu vay nợ thì là nhận tiền về -> Cũng là Thu (tùy logic, ở đây giữ nguyên code của bạn)
        if (debt.getType() == DebtType.BORROWING && category.getType() == CategoryType.INCOME) return true;
        return false;
    }

    @Override
    public boolean isExpense() {
        if (debt == null) return super.isExpense();
        if (debt.getType() == DebtType.BORROWING) return true; // Trả nợ -> Chi
        // Cho vay -> Chi tiền đi -> Chi
        if (debt.getType() == DebtType.LENDING && category.getType() == CategoryType.EXPENSE) return true;
        return false;
    }

    public Debt getDebt() { return debt; }
    public void setDebt(Debt debt) { this.debt = debt; }
}