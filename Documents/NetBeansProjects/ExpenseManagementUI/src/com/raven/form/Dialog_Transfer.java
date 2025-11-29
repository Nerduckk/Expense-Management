package com.raven.form;

import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.TransferTransaction;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.service.TransactionService;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.*;

public class Dialog_Transfer extends JDialog {

    private final TransactionService transactionService;

    private JComboBox<Account> comboFrom;
    private JComboBox<Account> comboTo;
    private JTextField txtAmount;
    private JTextField txtFee;
    private JTextField txtNote;

    public Dialog_Transfer(Frame parent, boolean modal, TransactionService service) {
        super(parent, modal);
        this.transactionService = service;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Chuyển tiền giữa các tài khoản");

        JPanel form = new JPanel();
        form.setLayout(new java.awt.GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Từ tài khoản:"));
        comboFrom = new JComboBox<>();
        form.add(comboFrom);

        form.add(new JLabel("Đến tài khoản:"));
        comboTo = new JComboBox<>();
        form.add(comboTo);

        form.add(new JLabel("Số tiền:"));
        txtAmount = new JTextField();
        form.add(txtAmount);

        form.add(new JLabel("Phí chuyển (tùy chọn):"));
        txtFee = new JTextField("0");
        form.add(txtFee);

        form.add(new JLabel("Ghi chú:"));
        txtNote = new JTextField();
        form.add(txtNote);

        // Nút
        JPanel bottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        bottom.add(btnCancel);
        bottom.add(btnSave);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        loadAccounts();

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
    }

    private void loadAccounts() {
        comboFrom.removeAllItems();
        comboTo.removeAllItems();

        for (Account a : DataStore.accounts) {
            comboFrom.addItem(a);
            comboTo.addItem(a);
        }
    }

    private void onSave() {
        Account from = (Account) comboFrom.getSelectedItem();
        Account to = (Account) comboTo.getSelectedItem();
        String amountStr = txtAmount.getText().trim();
        String feeStr = txtFee.getText().trim();
        String note = txtNote.getText().trim();

        if (from == null || to == null) {
            showMsg("Hãy chọn tài khoản nguồn và đích.");
            return;
        }

        if (from == to) {
            showMsg("Hai tài khoản không thể giống nhau.");
            return;
        }

        BigDecimal amount;
        BigDecimal fee;

        try {
            amount = new BigDecimal(amountStr);
            fee = new BigDecimal(feeStr.isEmpty() ? "0" : feeStr);
        } catch (Exception e) {
            showMsg("Số tiền không hợp lệ.");
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showMsg("Số tiền phải lớn hơn 0.");
            return;
        }

        long newId = DataStore.transactions.stream()
                .mapToLong(t -> t.getId() == null ? 0L : t.getId())
                .max()
                .orElse(0L) + 1;

        TransferTransaction tx = new TransferTransaction(
                newId,
                amount,
                LocalDate.now(),
                from,
                to,
                fee
        );
        tx.setNote(note);

        transactionService.addTransaction(tx);

        JOptionPane.showMessageDialog(this, "Đã chuyển tiền thành công!");
        dispose();
    }

    private void showMsg(String m) {
        JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
