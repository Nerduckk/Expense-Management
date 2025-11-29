package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.*;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.service.TransactionService;

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

        // Tạo DebtTransaction
        DebtTransaction txn = new DebtTransaction(
                DataStore.transactions.size() + 1L,
                amount,
                LocalDate.now(),
                acc,
                debt
        );

        // BORROWING (đi vay) → trả nợ → TRỪ tiền
        // LENDING   (cho vay) → thu nợ → CỘNG tiền

        if (debt.getType() == DebtType.BORROWING) {
            acc.setBalance(acc.getBalance().subtract(amount));
        } else {
            acc.setBalance(acc.getBalance().add(amount));
        }

        debt.getTransactions().add(txn);

        // Cập nhật số tiền còn lại
        if (debt.getType() == DebtType.BORROWING) {
            debt.setPaidAmount(debt.getPaidAmount().add(amount));
        } else {
            debt.setPaidAmount(debt.getPaidAmount().add(amount));
        }

        // Kiểm tra hoàn tất
        if (debt.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            debt.setStatus(DebtStatus.COMPLETED);
        }

        DataStore.transactions.add(txn);
        DataStore.saveData();

        JOptionPane.showMessageDialog(this, "Giao dịch đã được tạo.");
        dispose();
    }
}
