package com.raven.form;

import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtStatus;
import com.mycompany.appquanlychitieu.model.DebtType;
import com.mycompany.appquanlychitieu.service.DebtService;
import com.mycompany.appquanlychitieu.service.DataStore;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Dialog_Debt extends JDialog {

    private final DebtService debtService;
    private final Debt editingDebt;

    private JTextField txtName;
    private JComboBox<DebtType> comboType;
    private JTextField txtPerson;
    private JTextField txtPrincipal;
    private JTextField txtInterest;
    private JTextField txtDueDate;   // yyyy-MM-dd
    private JComboBox<DebtStatus> comboStatus;

    public Dialog_Debt(Frame parent,
                       boolean modal,
                       DebtService service,
                       Debt toEdit) {
        super(parent, modal);
        this.debtService = service;
        this.editingDebt = toEdit;

        initComponents();
        setLocationRelativeTo(parent);

        if (editingDebt != null) {
            loadData();
        }
    }

    private void initComponents() {
        setTitle(editingDebt == null ? "Thêm khoản nợ" : "Sửa khoản nợ");

        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tên khoản nợ
        mainPanel.add(new JLabel("Tên khoản nợ:"));
        txtName = new JTextField();
        mainPanel.add(txtName);

        // Loại
        mainPanel.add(new JLabel("Loại:"));
        comboType = new JComboBox<>(DebtType.values());
        mainPanel.add(comboType);

        // Người liên quan
        mainPanel.add(new JLabel("Người liên quan:"));
        txtPerson = new JTextField();
        mainPanel.add(txtPerson);

        // Số tiền gốc
        mainPanel.add(new JLabel("Số tiền gốc:"));
        txtPrincipal = new JTextField();
        mainPanel.add(txtPrincipal);

        // Lãi suất
        mainPanel.add(new JLabel("Lãi suất (%/năm):"));
        txtInterest = new JTextField();
        mainPanel.add(txtInterest);

        // Ngày đến hạn
        mainPanel.add(new JLabel("Ngày đến hạn (yyyy-MM-dd):"));
        txtDueDate = new JTextField();
        mainPanel.add(txtDueDate);

        // Trạng thái
        mainPanel.add(new JLabel("Trạng thái:"));
        comboStatus = new JComboBox<>(DebtStatus.values());
        mainPanel.add(comboStatus);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        getContentPane().setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private void loadData() {
        txtName.setText(editingDebt.getName());
        comboType.setSelectedItem(editingDebt.getType());
        txtPerson.setText(editingDebt.getPersonName());

        if (editingDebt.getPrincipalAmount() != null) {
            txtPrincipal.setText(editingDebt.getPrincipalAmount().toPlainString());
        }
        if (editingDebt.getInterestRate() != null) {
            txtInterest.setText(editingDebt.getInterestRate().toString());
        }
        if (editingDebt.getDueDate() != null) {
            txtDueDate.setText(editingDebt.getDueDate().toString());  // yyyy-MM-dd
        }
        comboStatus.setSelectedItem(editingDebt.getStatus());
    }

    private void onSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            showError("Tên khoản nợ không được để trống");
            return;
        }

        DebtType type = (DebtType) comboType.getSelectedItem();
        String person = txtPerson.getText().trim();

        // Số tiền gốc
        BigDecimal principal;
        try {
            principal = new BigDecimal(txtPrincipal.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Số tiền gốc không hợp lệ");
            return;
        }

        // Lãi suất
        Double interest = null;
        String interestStr = txtInterest.getText().trim();
        if (!interestStr.isEmpty()) {
            try {
                interest = Double.parseDouble(interestStr);
            } catch (NumberFormatException ex) {
                showError("Lãi suất không hợp lệ");
                return;
            }
        }

        // Ngày đến hạn
        LocalDate due = null;
        String dueStr = txtDueDate.getText().trim();
        if (!dueStr.isEmpty()) {
            try {
                due = LocalDate.parse(dueStr);   // format yyyy-MM-dd
            } catch (DateTimeParseException ex) {
                showError("Ngày đến hạn không hợp lệ (định dạng yyyy-MM-dd)");
                return;
            }
        }

        DebtStatus status = (DebtStatus) comboStatus.getSelectedItem();

        if (editingDebt == null) {
            long newId = DataStore.debts.stream()
                    .mapToLong(d -> d.getId() == null ? 0L : d.getId())
                    .max()
                    .orElse(0L) + 1;

            Debt d = new Debt(newId, name, type, principal, person);
            d.setInterestRate(interest);
            d.setDueDate(due);
            d.setStatus(status);

            debtService.add(d);
        } else {
            editingDebt.setName(name);
            editingDebt.setType(type);
            editingDebt.setPersonName(person);
            editingDebt.setPrincipalAmount(principal);
            editingDebt.setInterestRate(interest);
            editingDebt.setDueDate(due);
            editingDebt.setStatus(status);

            debtService.update(editingDebt);
        }

        JOptionPane.showMessageDialog(this, "Đã lưu khoản nợ");
        dispose();
    }

    private void showError(String m) {
        JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
