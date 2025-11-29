package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtStatus;
import com.mycompany.appquanlychitieu.model.DebtType;
import com.mycompany.appquanlychitieu.service.DebtService;

import java.awt.Color;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Form_Debts extends JPanel {

    private final DebtService debtService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public Form_Debts(DebtService ds) {
        this.debtService = ds;
        initComponents();
        loadDebtsToTable();
    }

    public Form_Debts() {
        this(AppContext.debtService);
    }

    private void initComponents() {
        setOpaque(false);

        JLabel lblTitle = new JLabel("Khoản nợ");
        lblTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 18));
        lblTitle.setForeground(new Color(76, 76, 76));

        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("↻ Làm mới");

        String[] columns = {
            "Tên",
            "Loại",
            "Người liên quan",
            "Gốc",
            "Còn lại",
            "Lãi (%)",
            "Ngày đến hạn",
            "Trạng thái"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);

        btnAdd.addActionListener(e -> openDialog(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadDebtsToTable());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editSelected();
                }
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblTitle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAdd)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnEdit)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnRefresh)))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTitle)
                        .addComponent(btnAdd)
                        .addComponent(btnEdit)
                        .addComponent(btnDelete)
                        .addComponent(btnRefresh))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addContainerGap())
        );
    }

    private void loadDebtsToTable() {
        tableModel.setRowCount(0);
        List<Debt> list = debtService.getAll();
        if (list == null) return;

        DecimalFormat moneyFmt = new DecimalFormat("#,##0");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Debt d : list) {
            if (d == null) continue;

            String loai = (d.getType() == DebtType.BORROWING) ? "Đi vay" : "Cho vay";
            String statusText;
            if (d.getStatus() == DebtStatus.ACTIVE) {
                statusText = "Đang theo dõi";
            } else if (d.getStatus() == DebtStatus.COMPLETED) {
                statusText = "Đã hoàn tất";
            } else {
                statusText = "Khó đòi";
            }

            String due = "";
            if (d.getDueDate() != null) {
                due = dateFmt.format(d.getDueDate());
            }

            tableModel.addRow(new Object[] {
                d.getName(),
                loai,
                d.getPersonName(),
                moneyFmt.format(d.getPrincipalAmount()),
                moneyFmt.format(d.getRemainingAmount()),
                d.getInterestRate(),
                due,
                statusText
            });
        }
    }

    private Debt getSelectedDebt() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        // Vì loadDebtsToTable() duyệt theo thứ tự debtService.getAll()
        // nên có thể map row -> list index
        List<Debt> list = debtService.getAll();
        if (row < 0 || row >= list.size()) return null;
        return list.get(row);
    }

    private void openDialog(Debt d) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_Debt dlg = new Dialog_Debt(parent, true, debtService, d);
        dlg.setVisible(true);
        loadDebtsToTable();
    }

    private void editSelected() {
        Debt d = getSelectedDebt();
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một khoản nợ.");
            return;
        }
        openDialog(d);
    }

    private void deleteSelected() {
        Debt d = getSelectedDebt();
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một khoản nợ.");
            return;
        }
        int c = JOptionPane.showConfirmDialog(
                this,
                "Xóa khoản nợ \"" + d.getName() + "\"?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (c == JOptionPane.YES_OPTION) {
            debtService.delete(d);
            loadDebtsToTable();
        }
    }
}
