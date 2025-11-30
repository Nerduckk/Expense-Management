package com.raven.form;

import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.RecurringSchedule;
import com.mycompany.appquanlychitieu.service.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import com.mycompany.appquanlychitieu.model.NormalTransaction;
import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.service.DataStore;

public class Form_RecurringSchedules extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnRunToday;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Form_RecurringSchedules() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));

        JLabel lblTitle = new JLabel("Giao dịch định kỳ");
        lblTitle.setFont(new Font("sansserif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(76, 76, 76));

        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnRunToday = new JButton("Tạo kỳ hôm nay");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());
        btnRunToday.addActionListener(e -> onRunToday());


        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        rightButtons.add(btnAdd);
        rightButtons.add(btnEdit);
        rightButtons.add(btnDelete);
        rightButtons.add(btnRefresh);
        rightButtons.add(btnRunToday);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(rightButtons, BorderLayout.EAST);

        String[] cols = {
                "ID", "Tên lịch", "Tài khoản", "Danh mục",
                "Số tiền", "Chu kỳ", "Bắt đầu", "Kết thúc",
                "Kỳ đã tạo", "Tổng kỳ", "Tự tạo"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (DataStore.recurringSchedules == null) return;

        for (RecurringSchedule rs : DataStore.recurringSchedules) {
            if (rs == null) continue;
            Account acc = rs.getAccount();
            Category cat = rs.getCategory();

            String accName = acc == null ? "" : acc.getName();
            String catName = cat == null ? "" : cat.getName();
            String amountStr = (rs.getAmount() == null) ? "" : rs.getAmount().toPlainString();
            String cycleStr = (rs.getCycle() == null) ? "" : rs.getCycle().name();
            String startStr = (rs.getStartDate() == null) ? "" : rs.getStartDate().format(dateFmt);
            String endStr = (rs.getEndDate() == null) ? "" : rs.getEndDate().format(dateFmt);
            String paidStr = (rs.getPaidTerms() == null) ? "0" : rs.getPaidTerms().toString();
            String totalStr = (rs.getTotalTerms() == null) ? "∞" : rs.getTotalTerms().toString();
            String autoStr = rs.isAutoCreate() ? "Có" : "Không";

            tableModel.addRow(new Object[]{
                    rs.getId(),
                    rs.getName(),
                    accName,
                    catName,
                    amountStr,
                    cycleStr,
                    startStr,
                    endStr,
                    paidStr,
                    totalStr,
                    autoStr
            });
        }
    }

    private RecurringSchedule findById(Long id) {
        if (DataStore.recurringSchedules == null) return null;
        for (RecurringSchedule rs : DataStore.recurringSchedules) {
            if (rs != null && id != null && id.equals(rs.getId())) return rs;
        }
        return null;
    }

    private Long getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        Object val = tableModel.getValueAt(modelRow, 0);
        if (val instanceof Long) return (Long) val;
        if (val instanceof Integer) return ((Integer) val).longValue();
        if (val instanceof String) {
            try {
                return Long.parseLong((String) val);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_RecurringSchedule dlg = new Dialog_RecurringSchedule(parent, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            RecurringSchedule rs = dlg.getSchedule();
            if (rs != null) {
                DataStore.recurringSchedules.add(rs);
                DataStore.saveData();
                loadData();
            }
        }
    }

    private void onEdit() {
        Long id = getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch để sửa");
            return;
        }
        RecurringSchedule rs = findById(id);
        if (rs == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lịch tương ứng");
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_RecurringSchedule dlg = new Dialog_RecurringSchedule(parent, rs);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            DataStore.saveData();
            loadData();
        }
    }

    private void onDelete() {
        Long id = getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch để xóa");
            return;
        }
        RecurringSchedule rs = findById(id);
        if (rs == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lịch tương ứng");
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Xóa lịch định kỳ \"" + rs.getName() + "\"?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        DataStore.recurringSchedules.remove(rs);
        DataStore.saveData();
        loadData();
    }
    private void onRunToday() {
    Long id = getSelectedId();
    if (id == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch để chạy.");
        return;
    }

    RecurringSchedule rs = findById(id);
    if (rs == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy lịch tương ứng.");
        return;
    }

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
}

}
