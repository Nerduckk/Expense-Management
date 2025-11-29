package com.raven.form;

import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.service.AccountService;
import com.mycompany.appquanlychitieu.service.DataStore;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Dialog_Account extends JDialog {

    private final AccountService accountService;
    private Account editingAccount;

    private JTextField txtName;
    private JTextField txtBalance;
    private JTextField txtCurrency;
    private JButton btnSave;
    private JButton btnCancel;

    public Dialog_Account(Frame parent,
                          boolean modal,
                          AccountService service,
                          Account toEdit) {
        super(parent, modal);
        this.accountService = service;
        this.editingAccount = toEdit;

        initComponents();
        setLocationRelativeTo(parent);

        if (editingAccount != null) {
            loadData();
        }
    }

    private void initComponents() {
        setTitle("Thêm / sửa ví");

        JPanel form = new JPanel();
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.setLayout(new java.awt.GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Tên ví:"));
        txtName = new JTextField();
        form.add(txtName);

        form.add(new JLabel("Số dư ban đầu:"));
        txtBalance = new JTextField();
        form.add(txtBalance);

        form.add(new JLabel("Tiền tệ:"));
        txtCurrency = new JTextField("VND");
        form.add(txtCurrency);

        JPanel bottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnCancel = new JButton("Hủy");
        btnSave = new JButton("Lưu");
        bottom.add(btnCancel);
        bottom.add(btnSave);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        pack();
    }

    private void loadData() {
        txtName.setText(editingAccount.getName());
        BigDecimal bal = editingAccount.getBalance();
        txtBalance.setText(bal == null ? "" : bal.toPlainString());
        txtCurrency.setText(editingAccount.getCurrency());
    }

    private void onSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên ví không được để trống");
            return;
        }

        String balanceStr = txtBalance.getText().trim();
        BigDecimal balance = BigDecimal.ZERO;
        if (!balanceStr.isEmpty()) {
            try {
                balance = new BigDecimal(balanceStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số dư không hợp lệ");
                return;
            }
        }

        String currency = txtCurrency.getText().trim();
        if (currency.isEmpty()) {
            currency = "VND";
        }

        if (editingAccount == null) {
            long newId = DataStore.accounts.stream()
                    .mapToLong(a -> a.getId() == null ? 0L : a.getId())
                    .max()
                    .orElse(0L) + 1;

            Account acc = new Account(newId, name, balance, currency);
            accountService.add(acc);
        } else {
            editingAccount.setName(name);
            editingAccount.setBalance(balance);
            editingAccount.setCurrency(currency);
            accountService.saveChanges();
        }

        dispose();
    }
}
