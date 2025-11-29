package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CategoryType;
import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtStatus;
import com.mycompany.appquanlychitieu.model.DebtTransaction;
import com.mycompany.appquanlychitieu.model.DebtType;
import com.mycompany.appquanlychitieu.service.DataStore;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class Dialog_PayDebt extends JDialog {

    private final Debt debt;
    private JTextField txtAmount;
    private JComboBox<Account> comboAccount;
    private JTextField txtRemain;
    private JLabel lblTypeValue;
    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.##");

    public Dialog_PayDebt(Frame parent, boolean modal, Debt debt) {
        super(parent, modal);
        this.debt = debt;
        initComponents();
    }

    private void initComponents() {
        setTitle("Trả / Thu nợ - " + debt.getName());
        setLayout(new BorderLayout());

        // ===== PANEL THÔNG TIN =====
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        infoPanel.add(new JLabel("Khoản nợ:"));
        infoPanel.add(new JLabel(debt.getName()));

        infoPanel.add(new JLabel("Loại:"));
        lblTypeValue = new JLabel(getTypeText());
        infoPanel.add(lblTypeValue);

        infoPanel.add(new JLabel("Còn lại:"));
        txtRemain = new JTextField(formatMoney(debt.getRemainingAmount()));
        txtRemain.setEditable(false);
        infoPanel.add(txtRemain);

        // ===== PANEL NHẬP LIỆU =====
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Số tiền
        inputPanel.add(new JLabel("Số tiền trả/thu:"));
        txtAmount = new JTextField();
        inputPanel.add(txtAmount);

        // Tài khoản
        inputPanel.add(new JLabel("Tài khoản:"));
        comboAccount = new JComboBox<>(
                AppContext.accountService.getAll().toArray(new Account[0])
        );
        inputPanel.add(comboAccount);

        // ===== NÚT DƯỚI FORM =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPayAll = new JButton("Trả hết");
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnPayAll.addActionListener(e -> fillFullAmount());
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());

        btnPanel.add(btnPayAll);
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(infoPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private String getTypeText() {
        if (debt.getType() == DebtType.BORROWING) {
            return "Đi vay (phải trả)";
        } else {
            return "Cho vay (phải thu)";
        }
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0";
        return moneyFmt.format(amount);
    }

    // Chọn category mặc định cho giao dịch nợ
    private Category findDefaultCategoryForDebt() {
        CategoryType targetType = (debt.getType() == DebtType.BORROWING)
                ? CategoryType.EXPENSE    // đi vay -> trả nợ = chi tiêu
                : CategoryType.INCOME;    // cho vay -> thu nợ = thu nhập

        Category fallback = null;
        for (Category c : DataStore.categories) {
            if (fallback == null) fallback = c;
            if (c.getType() == targetType) {
                if (c.getName() != null && c.getName().toLowerCase().contains("nợ")) {
                    return c; // ưu tiên danh mục có chữ "nợ"
                }
            }
        }
        return fallback;
    }

    // Nút "Trả hết" -> auto điền số tiền còn lại
    private void fillFullAmount() {
        BigDecimal remaining = debt.getRemainingAmount();
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Khoản nợ này đã thanh toán hết.");
            return;
        }
        txtAmount.setText(remaining.toPlainString());
    }

    private void save() {
        // ===== Validate số tiền =====
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

        // ===== Tài khoản =====
        Account acc = (Account) comboAccount.getSelectedItem();
        if (acc == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn tài khoản");
            return;
        }

        // Nếu đi vay và số dư tài khoản nhỏ hơn số trả -> hỏi lại
        if (debt.getType() == DebtType.BORROWING &&
                acc.getBalance() != null &&
                acc.getBalance().compareTo(amount) < 0) {

            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Số dư trong tài khoản (" + formatMoney(acc.getBalance()) +
                            ") nhỏ hơn số tiền muốn trả (" + formatMoney(amount) + ").\n"
                            + "Bạn vẫn muốn tiếp tục?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (opt != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // ===== Category =====
        Category category = findDefaultCategoryForDebt();
        if (category == null) {
            JOptionPane.showMessageDialog(this,
                    "Chưa có danh mục phù hợp cho khoản nợ này (thu/chi). Hãy tạo danh mục trước.");
            return;
        }

        // ===== Tạo ID giao dịch mới =====
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

        // Gắn vào danh sách giao dịch của khoản nợ
        if (debt.getTransactions() == null) {
            debt.setTransactions(new java.util.ArrayList<>());
        }
        debt.getTransactions().add(txn);

        // Nếu đã trả/thu hết -> hoàn tất
        if (debt.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            debt.setStatus(DebtStatus.COMPLETED);
        }

        // Đưa vào hệ thống giao dịch chung (cập nhật số dư, v.v.)
        AppContext.transactionService.addTransaction(txn);
        DataStore.saveData();

        JOptionPane.showMessageDialog(this, "Giao dịch đã được tạo.");
        dispose();
    }
}
