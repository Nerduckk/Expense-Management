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
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableCellRenderer;

public class Form_Debts extends JPanel {

    private final DebtService debtService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnPay;
    private JComboBox<String> cboStatusFilter;
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
    JLabel lblFilter = new JLabel("Lọc trạng thái:");
    cboStatusFilter = new JComboBox<>(new String[] {
        "Tất cả",
        "Đang theo dõi",
        "Đã hoàn tất",
        "Khó đòi"
    });
    btnAdd = new JButton("Thêm");
    btnEdit = new JButton("Sửa");
    btnDelete = new JButton("Xóa");
    btnRefresh = new JButton("↻ Làm mới");
    btnPay = new JButton("Trả/Thu nợ");      // <<< NHỚ DÒNG NÀY

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
    // Renderer tô màu theo loại & trạng thái
table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        java.awt.Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            c.setForeground(Color.BLACK);
            c.setBackground(Color.WHITE);
        }

        int modelRow = table.convertRowIndexToModel(row);
        String typeText = (String) table.getModel().getValueAt(modelRow, 1);   // cột Loại
        String statusText = (String) table.getModel().getValueAt(modelRow, 7); // cột Trạng thái

        if (!isSelected) {
            // Màu cho cột Loại
            if (column == 1) {
                if ("Đi vay".equals(typeText)) {
                    c.setForeground(new Color(220, 38, 38));      // đỏ
                } else { // Cho vay
                    c.setForeground(new Color(22, 163, 74));      // xanh lá
                }
            }

            // Màu cho cột Trạng thái
            if (column == 7) {
                if ("Đang theo dõi".equals(statusText)) {
                    c.setForeground(new Color(234, 179, 8));      // vàng
                } else if ("Đã hoàn tất".equals(statusText)) {
                    c.setForeground(new Color(107, 114, 128));    // xám
                } else if ("Khó đòi".equals(statusText)) {
                    c.setForeground(new Color(185, 28, 28));      // đỏ đậm
                }
            }
        }

        return c;
    }
});
    // Sự kiện
    btnAdd.addActionListener(e -> openDialog(null));
    btnEdit.addActionListener(e -> editSelected());
    btnPay.addActionListener(e -> paySelected());       // <<< GẮN ACTION
    btnDelete.addActionListener(e -> deleteSelected());
    btnRefresh.addActionListener(e -> loadDebtsToTable());
    cboStatusFilter.addActionListener(e -> loadDebtsToTable());
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

    // ----- HORIZONTAL -----
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
                        .addComponent(btnPay)       // <<< THÊM VÀO HORIZONTAL GROUP
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefresh)))
                .addContainerGap())
    );

    // ----- HORIZONTAL -----
layout.setHorizontalGroup(
    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblTitle)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(lblFilter)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cboStatusFilter, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnEdit)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnPay)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnDelete)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnRefresh)))
            .addContainerGap())
);

// ----- VERTICAL -----
layout.setVerticalGroup(
    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblTitle)
                .addComponent(lblFilter)
                .addComponent(cboStatusFilter)
                .addComponent(btnAdd)
                .addComponent(btnEdit)
                .addComponent(btnPay)
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
        String filter = (cboStatusFilter == null)
        ? "Tất cả"
        : (String) cboStatusFilter.getSelectedItem();
        DecimalFormat moneyFmt = new DecimalFormat("#,##0");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Debt d : list) {
            if (!"Tất cả".equals(filter)) {
            if ("Đang theo dõi".equals(filter) && d.getStatus() != DebtStatus.ACTIVE) {
        continue;
    }
            if ("Đã hoàn tất".equals(filter) && d.getStatus() != DebtStatus.COMPLETED) {
        continue;
    }
            if ("Khó đòi".equals(filter) && d.getStatus() != DebtStatus.BAD_DEBT) {
        continue;
    }
            }
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
    private void paySelected() {
    Debt d = getSelectedDebt();
    if (d == null) {
        JOptionPane.showMessageDialog(this, "Hãy chọn một khoản nợ.");
        return;
    }
    Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
    Dialog_PayDebt dlg = new Dialog_PayDebt(parent, true, d);
    dlg.setVisible(true);
    loadDebtsToTable();
    }
}
