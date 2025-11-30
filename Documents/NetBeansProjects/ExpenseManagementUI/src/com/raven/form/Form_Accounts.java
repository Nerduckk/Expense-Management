package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.service.AccountService;

import java.awt.Color;
import java.awt.Frame;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Form_Accounts extends JPanel {

    private final AccountService accountService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public Form_Accounts(AccountService service) {
        this.accountService = service;
        initComponents();
        loadAccountsToTable();
    }

    public Form_Accounts() {
        this(AppContext.accountService);
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Quản lý ví / tài khoản");
        lblTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 16));
        lblTitle.setForeground(new Color(76, 76, 76));

        btnAdd = new JButton("+ Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        tableModel = new DefaultTableModel(
                new Object[]{"Tên ví", "Số dư", "Tiền tệ"},
                0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);

        btnAdd.addActionListener(e -> openDialog(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadAccountsToTable());

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
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblTitle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                             GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRefresh)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnDelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnEdit)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnAdd)))
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
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addContainerGap())
        );
    }

    private void loadAccountsToTable() {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,##0.##");

        List<Account> list = accountService.getAll();
        for (Account a : list) {
            if (a == null) continue;
            BigDecimal bal = a.getBalance();
            String balStr = bal == null ? "" : df.format(bal);
            tableModel.addRow(new Object[]{
                    a.getName(),
                    balStr,
                    a.getCurrency()
            });
        }
    }

    private void openDialog(Account acc) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_Account dlg = new Dialog_Account(parent, true, accountService, acc);
        dlg.setVisible(true);
        loadAccountsToTable();
    }

    private Account getSelectedAccount() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        List<Account> list = accountService.getAll();
        if (row < 0 || row >= list.size()) return null;
        return list.get(row);
    }

    private void editSelected() {
        Account acc = getSelectedAccount();
        if (acc == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa");
            return;
        }
        openDialog(acc);
    }

    private void deleteSelected() {
        Account acc = getSelectedAccount();
        if (acc == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Xóa ví \"" + acc.getName() + "\"?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            accountService.delete(acc);
            loadAccountsToTable();
        }
    }
}
