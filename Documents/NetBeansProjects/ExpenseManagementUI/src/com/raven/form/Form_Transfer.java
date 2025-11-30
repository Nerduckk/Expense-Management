package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.TransferTransaction;

import java.awt.Frame;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Form_Transfer extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton btnNew;

    public Form_Transfer() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new java.awt.BorderLayout());

        JLabel lbl = new JLabel("Lịch sử chuyển tiền");
        lbl.setFont(new java.awt.Font("sansserif", 1, 16));

        btnNew = new JButton("Chuyển tiền");
        btnNew.addActionListener(e -> openDialog());

        JPanel top = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        top.add(lbl);
        top.add(btnNew);

        model = new DefaultTableModel(
                new Object[]{"Ngày", "Từ", "Đến", "Số tiền", "Phí", "Ghi chú"},
                0
        );
        table = new JTable(model);
        table.setRowHeight(26);

        add(top, java.awt.BorderLayout.NORTH);
        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);

        DecimalFormat df = new DecimalFormat("#,##0.##");

        AppContext.transactionService.getAll().stream()
                .filter(t -> t instanceof TransferTransaction)
                .map(t -> (TransferTransaction) t)
                .forEach(tx -> model.addRow(new Object[]{
                        tx.getDate().toString(),
                        tx.getSourceAccount().getName(),
                        tx.getToAccount().getName(),
                        df.format(tx.getAmount()),
                        df.format(tx.getTransferFee()),
                        tx.getNote()
                }));
    }

    private void openDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_Transfer dlg = new Dialog_Transfer(parent, true, AppContext.transactionService);
        dlg.setVisible(true);
        loadData();
    }
}
