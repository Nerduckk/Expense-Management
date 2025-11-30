package com.raven.form;

import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Dialog_DebtHistory extends JDialog {

    private final Debt debt;
    private JTable table;
    private DefaultTableModel model;
    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.##");
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Dialog_DebtHistory(Frame parent, boolean modal, Debt debt) {
        super(parent, modal);
        this.debt = debt;
        initComponents();
    }

    private void initComponents() {
        setTitle("Lịch sử trả/thu - " + debt.getName());
        setLayout(new BorderLayout(0, 10));

        JLabel lblTitle = new JLabel("Lịch sử trả/thu cho khoản nợ: " + debt.getName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        String[] cols = {"Ngày", "Loại", "Số tiền", "Tài khoản", "Ghi chú"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(table);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnClose);

        add(lblTitle, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadHistory();

        setSize(600, 400);
        setLocationRelativeTo(getParent());
    }

    private void loadHistory() {
        model.setRowCount(0);

        List<DebtTransaction> txns = (debt.getTransactions() == null)
                ? List.of()
                : debt.getTransactions().stream()
                .sorted(Comparator.comparing(DebtTransaction::getDate).reversed())
                .collect(Collectors.toList());

        if (txns.isEmpty()) {
            // Không có giao dịch -> hiển thị 1 dòng thông báo
            model.addRow(new Object[]{"(Chưa có giao dịch)", "", "", "", ""});
            return;
        }

        for (DebtTransaction t : txns) {
            String ngay = t.getDate() == null ? "" : t.getDate().format(dateFmt);
            String loai = (debt.getType() != null && debt.getType().name().contains("BORROW"))
                    ? "Trả nợ"
                    : "Thu nợ";
            BigDecimal amt = t.getAmount() == null ? BigDecimal.ZERO : t.getAmount();
            Account acc = t.getAccount();
            String accName = acc == null ? "" : acc.getName();
            String note = t.getNote() == null ? "" : t.getNote();

            model.addRow(new Object[]{
                    ngay,
                    loai,
                    moneyFmt.format(amt),
                    accName,
                    note
            });
        }
    }
}
