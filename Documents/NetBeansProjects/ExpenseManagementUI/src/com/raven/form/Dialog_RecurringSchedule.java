package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CycleType;
import com.mycompany.appquanlychitieu.model.RecurringSchedule;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class Dialog_RecurringSchedule extends JDialog {

    private RecurringSchedule schedule;   // null = tạo mới, != null = sửa
    private boolean saved = false;

    private JTextField txtName;
    private JComboBox<Account> cboAccount;
    private JComboBox<Category> cboCategory;
    private JTextField txtAmount;
    private JComboBox<CycleType> cboCycle;
    private JDateChooser dcStartDate;
    private JDateChooser dcEndDate;
    private JTextField txtTotalTerms;
    private JCheckBox chkAutoCreate;

    public Dialog_RecurringSchedule(Frame parent, RecurringSchedule schedule) {
        super(parent, true);
        this.schedule = schedule;
        initComponents();
        if (schedule != null) {
            fillForm();
        }
    }

    private void initComponents() {
        setTitle(schedule == null ? "Thêm lịch định kỳ" : "Sửa lịch định kỳ");
        setLayout(new BorderLayout(0, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtName = new JTextField();
        cboAccount = new JComboBox<>(
                AppContext.accountService.getAll().toArray(new Account[0])
        );
        cboCategory = new JComboBox<>(
                DataStore.categories.toArray(new Category[0])
        );
        txtAmount = new JTextField();

        cboCycle = new JComboBox<>(CycleType.values());

        dcStartDate = new JDateChooser();
        dcStartDate.setDateFormatString("dd/MM/yyyy");
        dcStartDate.setDate(new Date());

        dcEndDate = new JDateChooser();
        dcEndDate.setDateFormatString("dd/MM/yyyy");

        txtTotalTerms = new JTextField();
        chkAutoCreate = new JCheckBox("Tự tạo giao dịch", true);

        formPanel.add(new JLabel("Tên lịch:"));
        formPanel.add(txtName);

        formPanel.add(new JLabel("Tài khoản:"));
        formPanel.add(cboAccount);

        formPanel.add(new JLabel("Danh mục:"));
        formPanel.add(cboCategory);

        formPanel.add(new JLabel("Số tiền mỗi kỳ:"));
        formPanel.add(txtAmount);

        formPanel.add(new JLabel("Chu kỳ:"));
        formPanel.add(cboCycle);

        formPanel.add(new JLabel("Ngày bắt đầu:"));
        formPanel.add(dcStartDate);

        formPanel.add(new JLabel("Ngày kết thúc (optional):"));
        formPanel.add(dcEndDate);

        formPanel.add(new JLabel("Tổng số kỳ (trống = vô hạn):"));
        formPanel.add(txtTotalTerms);

        formPanel.add(new JLabel(""));
        formPanel.add(chkAutoCreate);

        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> {
            saved = false;
            dispose();
        });
        btnSave.addActionListener(e -> onSave());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void fillForm() {
        txtName.setText(schedule.getName());
        if (schedule.getAccount() != null) {
            cboAccount.setSelectedItem(schedule.getAccount());
        }
        if (schedule.getCategory() != null) {
            cboCategory.setSelectedItem(schedule.getCategory());
        }
        if (schedule.getAmount() != null) {
            txtAmount.setText(schedule.getAmount().toPlainString());
        }
        if (schedule.getCycle() != null) {
            cboCycle.setSelectedItem(schedule.getCycle());
        }
        if (schedule.getStartDate() != null) {
            Date d = Date.from(schedule.getStartDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            dcStartDate.setDate(d);
        }
        if (schedule.getEndDate() != null) {
            Date d = Date.from(schedule.getEndDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            dcEndDate.setDate(d);
        }
        if (schedule.getTotalTerms() != null) {
            txtTotalTerms.setText(schedule.getTotalTerms().toString());
        }
        chkAutoCreate.setSelected(schedule.isAutoCreate());
    }

    private void onSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên lịch không được để trống");
            return;
        }

        Account account = (Account) cboAccount.getSelectedItem();
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản");
            return;
        }

        Category category = (Category) cboCategory.getSelectedItem();
        if (category == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(txtAmount.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ");
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền phải > 0");
            return;
        }

        CycleType cycle = (CycleType) cboCycle.getSelectedItem();
        if (cycle == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chu kỳ");
            return;
        }

        Date startUtil = dcStartDate.getDate();
        if (startUtil == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu");
            return;
        }
        LocalDate startDate = startUtil.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate endDate = null;
        if (dcEndDate.getDate() != null) {
            endDate = dcEndDate.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu");
                return;
            }
        }

        Integer totalTerms = null;
        String totalText = txtTotalTerms.getText().trim();
        if (!totalText.isEmpty()) {
            try {
                int v = Integer.parseInt(totalText);
                if (v <= 0) {
                    JOptionPane.showMessageDialog(this, "Tổng số kỳ phải > 0");
                    return;
                }
                totalTerms = v;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tổng số kỳ không hợp lệ");
                return;
            }
        }

        boolean autoCreate = chkAutoCreate.isSelected();

        if (schedule == null) {
            long newId = DataStore.recurringSchedules.stream()
                    .mapToLong(rs -> rs.getId() == null ? 0L : rs.getId())
                    .max()
                    .orElse(0L) + 1L;

            schedule = new RecurringSchedule(
                    newId,
                    name,
                    account,
                    category,
                    amount,
                    cycle,
                    startDate,
                    totalTerms,
                    LocalTime.of(9, 0),
                    autoCreate,
                    endDate
            );
        } else {
            schedule.setName(name);
            schedule.setAccount(account);
            schedule.setCategory(category);
            schedule.setAmount(amount);
            schedule.setCycle(cycle);
            schedule.setStartDate(startDate);
            schedule.setEndDate(endDate);
            schedule.setTotalTerms(totalTerms);
            schedule.setAutoCreate(autoCreate);
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public RecurringSchedule getSchedule() {
        return schedule;
    }
}
