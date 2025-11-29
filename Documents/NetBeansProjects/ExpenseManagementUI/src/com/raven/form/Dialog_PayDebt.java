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

    // --- CHẶN TRẢ VƯỢT NỢ ---
    BigDecimal remaining = debt.getRemainingAmount();
    if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
        JOptionPane.showMessageDialog(this,
                "Khoản nợ này đã được thanh toán hết, không thể trả/thu thêm.");
        return;
    }
    if (amount.compareTo(remaining) > 0) {
        JOptionPane.showMessageDialog(this,
                "Số tiền vượt quá số nợ còn lại (" + remaining.toPlainString() + ").");
        return;
    }
    // -------------------------

    Account acc = (Account) comboAccount.getSelectedItem();
    if (acc == null) {
        JOptionPane.showMessageDialog(this, "Chưa chọn tài khoản");
        return;
    }

    // ... đoạn phía dưới: chọn Category, tạo DebtTransaction, add vào Debt, 
    //   gọi AppContext.transactionService.addTransaction(txn) và set COMPLETED nếu hết nợ ...
}
}
