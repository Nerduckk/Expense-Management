package com.raven.form;

import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CategoryType;
import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtTransaction;
import com.mycompany.appquanlychitieu.model.DebtType;
import com.mycompany.appquanlychitieu.model.DebtStatus;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.AppContext;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Dialog_PayDebt extends JDialog {
    private final Debt debt;
    private JTextField txtAmount;
    private JComboBox<Account> comboAccount;
    public Dialog_PayDebt(Frame parent, boolean modal, Debt debt) {
        super(parent, modal);
        this.debt = debt;
        initComponents();
    }
    private Category findDefaultCategoryForDebt() {
    // Đi vay -> trả nợ là CHI TIÊU
    // Cho vay -> thu nợ là THU NHẬP
    CategoryType targetType = (debt.getType() == DebtType.BORROWING)
            ? CategoryType.EXPENSE
            : CategoryType.INCOME;

    Category fallback = null;
    for (Category c : DataStore.categories) {
        if (fallback == null) fallback = c;           // phòng khi không tìm được type khớp
        if (c.getType() == targetType) {
            // ưu tiên danh mục có chữ "nợ" nếu có
            if (c.getName() != null && c.getName().toLowerCase().contains("nợ")) {
                return c;
            }
        }
    }
    return fallback;   // có thể null nếu không có danh mục nào
}
    private void initComponents() {
        setTitle("Trả / Thu nợ - " + debt.getName());
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Số tiền
        panel.add(new JLabel("Số tiền:"));
        txtAmount = new JTextField();
        panel.add(txtAmount);

        // Tài khoản
        panel.add(new JLabel("Tài khoản:"));
        comboAccount = new JComboBox<>(
                AppContext.accountService.getAll().toArray(new Account[0])
        );
        panel.add(comboAccount);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getParent());
    }
    private void save() {
    BigDecimal amount;
    try {
        amount = new BigDecimal(txtAmount.getText().trim());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ");
        return;
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        JOptionPane.showMessageDialog(this, "Số tiền phải > 0");
        return;
    }

    Account acc = (Account) comboAccount.getSelectedItem();
    if (acc == null) {
        JOptionPane.showMessageDialog(this, "Chưa chọn tài khoản");
        return;
    }

    Category category = findDefaultCategoryForDebt();
    if (category == null) {
        JOptionPane.showMessageDialog(this,
                "Chưa có danh mục phù hợp cho khoản nợ này (thu/chi). Hãy tạo danh mục trước.");
        return;
    }

    // Tạo id mới giống Dialog_Transaction
    long id = DataStore.transactions.stream()
            .mapToLong(t -> t.getId() == null ? 0L : t.getId())
            .max()
            .orElse(0L) + 1;

    DebtTransaction txn = new DebtTransaction(
            id,
            amount,
            LocalDate.now(),
            acc,
            category,
            debt
    );
    txn.setNote("Giao dịch cho khoản nợ: " + debt.getName());

    // Gắn giao dịch này vào danh sách của khoản nợ
    if (debt.getTransactions() == null) {
        debt.setTransactions(new java.util.ArrayList<>());
    }
    debt.getTransactions().add(txn);

    // Cập nhật trạng thái nếu đã trả/thu đủ
    if (debt.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
        debt.setStatus(DebtStatus.COMPLETED);
    }

    // Đưa vào hệ thống giao dịch chung (tự xử lý số dư tài khoản)
    AppContext.transactionService.addTransaction(txn);

    DataStore.saveData();

    JOptionPane.showMessageDialog(this, "Giao dịch đã được tạo.");
    dispose();
}

}
