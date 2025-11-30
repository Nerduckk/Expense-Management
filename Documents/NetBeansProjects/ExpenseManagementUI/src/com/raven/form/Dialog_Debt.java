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
import com.toedter.calendar.JDateChooser;
import java.awt.FlowLayout;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.SwingUtilities;


public class Dialog_Debt extends JDialog {

    private final DebtService debtService;
    private final Debt editingDebt;

    private JTextField txtName;
    private JComboBox<DebtType> comboType;
    private JTextField txtPerson;
    private JTextField txtPrincipal;
    private JTextField txtInterest;
    private JDateChooser dateDueChooser;  
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
        mainPanel.add(new JLabel("Ngày đến hạn:"));

        dateDueChooser = new JDateChooser();
        dateDueChooser.setDateFormatString("dd/MM/yyyy");      // hiển thị đẹp kiểu VN
        dateDueChooser.setPreferredSize(new java.awt.Dimension(120, 24));
        // Có thể set default là ngày hôm nay:
        dateDueChooser.setDate(new Date());
        mainPanel.add(dateDueChooser);


        // Trạng thái
        mainPanel.add(new JLabel("Trạng thái:"));
        comboStatus = new JComboBox<>(DebtStatus.values());
        mainPanel.add(comboStatus);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnHistory = new JButton("Lịch sử trả/thu");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> dispose());
        btnHistory.addActionListener(e -> openHistory());
        btnSave.addActionListener(e -> onSave());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnHistory);
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
        LocalDate ld = editingDebt.getDueDate();
        Date d = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        dateDueChooser.setDate(d);
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
        Date utilDate = dateDueChooser.getDate();
        if (utilDate == null) {
           JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày đến hạn.");
        return;
        }

        LocalDate dueDate = utilDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
        DebtStatus status = (DebtStatus) comboStatus.getSelectedItem();

        if (editingDebt == null) {
            long newId = DataStore.debts.stream()
                    .mapToLong(d -> d.getId() == null ? 0L : d.getId())
                    .max()
                    .orElse(0L) + 1;

            Debt d = new Debt(newId, name, type, principal, person);
            d.setInterestRate(interest);
            d.setDueDate(dueDate);
            d.setStatus(status);

            debtService.add(d);
        } else {
            editingDebt.setName(name);
            editingDebt.setType(type);
            editingDebt.setPersonName(person);
            editingDebt.setPrincipalAmount(principal);
            editingDebt.setInterestRate(interest);
            editingDebt.setDueDate(dueDate);
            editingDebt.setStatus(status);

            debtService.update(editingDebt);
        }

        JOptionPane.showMessageDialog(this, "Đã lưu khoản nợ");
        dispose();
    }

    private void showError(String m) {
        JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    private void openHistory() {
    if (editingDebt == null || editingDebt.getId() == null) {
        JOptionPane.showMessageDialog(
                this,
                "Chỉ xem lịch sử cho khoản nợ đã được lưu.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );
        return;
    }

    Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
    Dialog_DebtHistory dlg = new Dialog_DebtHistory(parent, true, editingDebt);
    dlg.setVisible(true);
}
}
