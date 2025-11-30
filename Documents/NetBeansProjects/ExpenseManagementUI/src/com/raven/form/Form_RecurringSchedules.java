package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CycleType;
import com.mycompany.appquanlychitieu.model.NormalTransaction;
import com.mycompany.appquanlychitieu.model.RecurringSchedule;
import com.mycompany.appquanlychitieu.service.DataStore;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Quản lý các RecurringSchedule (giao dịch định kỳ).
 * Thêm:
 *  - Tổng số kỳ (totalTerms)
 *  - Ngày kết thúc dự kiến: tính từ startDate + cycle * (totalTerms - 1)
 */
public class Form_RecurringSchedules extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    private final JTextField txtName;
    private final JTextField txtAmount;
    private final JDateChooser dcStartDate;
    private final JComboBox<Account> cbAccount;
    private final JComboBox<Category> cbCategory;
    private final JComboBox<CycleType> cbCycle;
    private final JSpinner spTotalTerms;
    private final JTextField txtEndDate;
    private final JCheckBox chkAutoCreate;

    private final JButton btnNew;
    private final JButton btnSave;
    private final JButton btnDelete;
    private final JButton btnCreateToday;

    private RecurringSchedule editing;   // null = thêm mới

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Form_RecurringSchedules() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // ===== BẢNG DANH SÁCH =====
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Tên", "Tài khoản", "Danh mục",
                        "Số tiền", "Chu kỳ", "Ngày bắt đầu",
                        "Đã tạo", "Tổng kỳ", "Tự tạo"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== FORM BÊN DƯỚI =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        txtName = new JTextField(20);
        txtAmount = new JTextField(20);
        dcStartDate = new JDateChooser();
        dcStartDate.setDateFormatString("yyyy-MM-dd");
        dcStartDate.setOpaque(false);

        cbAccount = new JComboBox<>();
        cbCategory = new JComboBox<>();
        cbCycle = new JComboBox<>(CycleType.values());
        spTotalTerms = new JSpinner(new SpinnerNumberModel(12, 1, 360, 1));
        txtEndDate = new JTextField(20);
        txtEndDate.setEditable(false);
        txtEndDate.setFocusable(false);
        txtEndDate.setBackground(UIManager.getColor("TextField.inactiveBackground"));
        chkAutoCreate = new JCheckBox("Tự tạo giao dịch đến hạn");

        btnNew = new JButton("Mới");
        btnSave = new JButton("Lưu");
        btnDelete = new JButton("Xóa");
        btnCreateToday = new JButton("Tạo kỳ hôm nay");

        int row = 0;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Tên:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(txtName, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Số tiền:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(txtAmount, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(dcStartDate, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Tài khoản:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(cbAccount, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Danh mục:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(cbCategory, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Chu kỳ:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(cbCycle, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Tổng số kỳ:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(spTotalTerms, g); row++;

        g.gridx = 0; g.gridy = row; g.weightx = 0;
        formPanel.add(new JLabel("Kết thúc dự kiến:"), g);
        g.gridx = 1; g.weightx = 1;
        formPanel.add(txtEndDate, g); row++;

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.weightx = 1;
        formPanel.add(chkAutoCreate, g); row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnNew);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnCreateToday);

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        formPanel.add(buttonPanel, g);

        add(formPanel, BorderLayout.SOUTH);

        initComboData();
        reloadTable();
        initActions();
        applyAutoTotalTermsForCurrentCycle();
        updateEndDatePreview();
    }

    private void initComboData() {
        cbAccount.removeAllItems();
        List<Account> accs = DataStore.accounts;
        if (accs != null) {
            for (Account a : accs) {
                cbAccount.addItem(a);
            }
        }

        cbCategory.removeAllItems();
        List<Category> cats = DataStore.categories;
        if (cats != null) {
            for (Category c : cats) {
                cbCategory.addItem(c);
            }
        }
    }

    private void reloadTable() {
        tableModel.setRowCount(0);
        List<RecurringSchedule> list = DataStore.recurringSchedules;
        if (list == null) return;

        for (RecurringSchedule rs : list) {
            if (rs == null) continue;

            String accName = (rs.getAccount() == null) ? "" : rs.getAccount().getName();
            String catName = (rs.getCategory() == null) ? "" : rs.getCategory().getName();
            String amountStr = (rs.getAmount() == null) ? "" : rs.getAmount().toPlainString();
            String cycleStr = (rs.getCycle() == null) ? "" : rs.getCycle().name();
            String startStr = (rs.getStartDate() == null) ? "" : rs.getStartDate().format(dateFmt);
            String paidStr = (rs.getPaidTerms() == null) ? "0" : rs.getPaidTerms().toString();
            String totalStr = (rs.getTotalTerms() == null) ? "∞" : rs.getTotalTerms().toString();
            String autoStr = rs.isAutoCreate() ? "Có" : "Không";

            tableModel.addRow(new Object[]{
                    rs.getName(),
                    accName,
                    catName,
                    amountStr,
                    cycleStr,
                    startStr,
                    paidStr,
                    totalStr,
                    autoStr
            });
        }
    }

    private void initActions() {
        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveCurrent());
        btnDelete.addActionListener(e -> deleteSelected());
        btnCreateToday.addActionListener(e -> createOneForToday());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadFromSelectedRow();
            }
        });

        cbCycle.addActionListener(e -> {
            applyAutoTotalTermsForCurrentCycle();
            updateEndDatePreview();
        });

        dcStartDate.addPropertyChangeListener("date", evt -> updateEndDatePreview());
        spTotalTerms.addChangeListener(e -> updateEndDatePreview());
    }

    private void clearForm() {
        editing = null;
        txtName.setText("");
        txtAmount.setText("");
        dcStartDate.setDate(null);
        cbAccount.setSelectedIndex(cbAccount.getItemCount() > 0 ? 0 : -1);
        cbCategory.setSelectedIndex(cbCategory.getItemCount() > 0 ? 0 : -1);
        cbCycle.setSelectedIndex(0);
        applyAutoTotalTermsForCurrentCycle();
        txtEndDate.setText("");
        chkAutoCreate.setSelected(true);
        table.clearSelection();
    }

    private void loadFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        List<RecurringSchedule> list = DataStore.recurringSchedules;
        if (list == null || row >= list.size()) return;

        RecurringSchedule rs = list.get(row);
        editing = rs;

        txtName.setText(rs.getName() == null ? "" : rs.getName());
        txtAmount.setText(rs.getAmount() == null ? "" : rs.getAmount().toPlainString());
        if (rs.getStartDate() != null) {
            dcStartDate.setDate(java.sql.Date.valueOf(rs.getStartDate()));
        } else {
            dcStartDate.setDate(null);
        }
        cbAccount.setSelectedItem(rs.getAccount());
        cbCategory.setSelectedItem(rs.getCategory());
        cbCycle.setSelectedItem(rs.getCycle());
        chkAutoCreate.setSelected(rs.isAutoCreate());

        Integer total = rs.getTotalTerms();
        if (total != null && total > 0) {
            spTotalTerms.setValue(total);
        } else {
            applyAutoTotalTermsForCurrentCycle();
        }
        updateEndDatePreview();
    }

    /**
     * Tự tính tổng số kỳ (totalTerms) mặc định theo chu kỳ.
     * Bạn có thể chỉnh lại mapping này nếu muốn.
     */
    private Integer calcAutoTotalTerms(CycleType cycle) {
        if (cycle == null) return null;
        switch (cycle) {
            case DAILY:
                return 30;   // 30 ngày
            case WEEKLY:
                return 12;   // 12 tuần
            case MONTHLY:
                return 12;   // 12 tháng
            case YEARLY:
                return 3;    // 3 năm
            default:
                return null;
        }
    }

    private void applyAutoTotalTermsForCurrentCycle() {
        CycleType cycle = (CycleType) cbCycle.getSelectedItem();
        Integer autoTerms = calcAutoTotalTerms(cycle);
        if (autoTerms != null) {
            spTotalTerms.setValue(autoTerms);
        }
    }

    private void updateEndDatePreview() {
        Date selected = dcStartDate.getDate();
        CycleType cycle = (CycleType) cbCycle.getSelectedItem();
        int total = (Integer) spTotalTerms.getValue();

        if (selected == null || cycle == null || total <= 0) {
            txtEndDate.setText("");
            return;
        }

        LocalDate start = selected.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate end = start;

        switch (cycle) {
            case DAILY:
                end = start.plusDays(total - 1L);
                break;
            case WEEKLY:
                end = start.plusWeeks(total - 1L);
                break;
            case MONTHLY:
                end = start.plusMonths(total - 1L);
                break;
            case YEARLY:
                end = start.plusYears(total - 1L);
                break;
            default:
                break;
        }

        txtEndDate.setText(end.format(dateFmt));
    }

    private void saveCurrent() {
        try {
            String name = txtName.getText().trim();
            String amountStr = txtAmount.getText().trim();
            Date selected = dcStartDate.getDate();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên không được để trống.");
                return;
            }
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số tiền không được để trống.");
                return;
            }
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được để trống.");
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);
            LocalDate start = selected.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            Account acc = (Account) cbAccount.getSelectedItem();
            Category cat = (Category) cbCategory.getSelectedItem();
            CycleType cycle = (CycleType) cbCycle.getSelectedItem();
            boolean auto = chkAutoCreate.isSelected();

            if (editing == null) {
                editing = new RecurringSchedule();
            }

            editing.setName(name);
            editing.setAmount(amount);
            editing.setStartDate(start);
            editing.setAccount(acc);
            editing.setCategory(cat);
            editing.setCycle(cycle);
            editing.setAutoCreate(auto);

            int totalTerms = (Integer) spTotalTerms.getValue();
            if (totalTerms <= 0) {
                JOptionPane.showMessageDialog(this, "Tổng số kỳ phải lớn hơn 0.");
                return;
            }
            editing.setTotalTerms(totalTerms);

            if (!DataStore.recurringSchedules.contains(editing)) {
                DataStore.recurringSchedules.add(editing);
            }

            DataStore.saveData();
            reloadTable();
            JOptionPane.showMessageDialog(this, "Đã lưu lịch định kỳ.");
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn dòng để xóa.");
            return;
        }

        List<RecurringSchedule> list = DataStore.recurringSchedules;
        if (list == null || row >= list.size()) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ.");
            return;
        }

        RecurringSchedule rs = list.get(row);
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Xóa lịch định kỳ: " + rs.getName() + " ?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        list.remove(row);
        DataStore.saveData();
        reloadTable();
        clearForm();
    }

    /**
     * Tạo 1 giao dịch định kỳ cho HÔM NAY từ schedule đang chọn (nếu đến hạn).
     */
    private void createOneForToday() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một lịch định kỳ tương ứng.");
            return;
        }

        List<RecurringSchedule> list = DataStore.recurringSchedules;
        if (list == null || row >= list.size()) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ.");
            return;
        }

        RecurringSchedule rs = list.get(row);
        LocalDate today = LocalDate.now();
        NormalTransaction txn = rs.createTransactionIfDue(today);
        if (txn == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hôm nay không phải kỳ đến hạn (hoặc lịch đã hết số kỳ)."
            );
            return;
        }

        AppContext.transactionService.addTransaction(txn);
        DataStore.saveData();

        JOptionPane.showMessageDialog(
                this,
                "Đã tạo 1 giao dịch định kỳ cho hôm nay:\n" +
                        txn.getName() + " - " + txn.getAmount()
        );
        reloadTable();
    }
}
