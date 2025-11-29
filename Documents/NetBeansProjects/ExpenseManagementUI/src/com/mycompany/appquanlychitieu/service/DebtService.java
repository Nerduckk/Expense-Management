package com.mycompany.appquanlychitieu.service;
import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtStatus;
import com.mycompany.appquanlychitieu.service.DataStore;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class DebtService {

    public List<Debt> getAll() {
        return DataStore.debts;
    }

    public void add(Debt d) {
        DataStore.debts.add(d);
        DataStore.saveData();
    }

    public void update(Debt d) {
        // nếu dùng id tự tăng thì tìm theo id, replace lại trong list
        DataStore.saveData();
    }

    public void delete(Debt d) {
        DataStore.debts.remove(d);
        DataStore.saveData();
    }

    // Lấy các khoản nợ còn ACTIVE, đến hạn trước 1–2 tuần nữa
    public List<Debt> getUpcomingDebts(LocalDate from, LocalDate to) {
        return DataStore.debts.stream()
                .filter(Objects::nonNull)
                .filter(d -> d.getStatus() == DebtStatus.ACTIVE)
                .filter(d -> d.getDueDate() != null
                          && !d.getDueDate().isBefore(from)
                          && !d.getDueDate().isAfter(to))
                .toList();
    }
}
