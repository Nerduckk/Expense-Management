package com.raven.form;

import java.math.BigDecimal;
import javax.swing.*;
import java.awt.*;

public class Form_BudgetSettings extends JPanel {

    // ===== CẤU HÌNH DÙNG CHUNG TOÀN APP (chỉ ở GUI, không backend) =====
    private static BigDecimal currentLimit = BigDecimal.ZERO;
    private static boolean limitEnabled = false;
    private static int warnPercent = 80;   // cảnh báo từ 80%

    public static BigDecimal getCurrentLimit() {
        return currentLimit;
    }

    public static boolean isLimitEnabled() {
        return limitEnabled;
    }

    public static int getWarnPercent() {
        return warnPercent;
    }

    // ======= GUI =======
    private JTextField txtAmount;
    private JTextField txtWarn;
    private JCheckBox chkEnable;
    private JButton btnSave;

    public Form_BudgetSettings() {
        initComponents();
        loadCurrentConfig();
        initActions();
    }

    private void initComponents() {
        setOpaque(false);

        JLabel lblTitle = new JLabel("Hạn mức chi tiêu tháng");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));

        JLabel lblAmount = new JLabel("Hạn mức (VND):");
        JLabel lblWarn   = new JLabel("Cảnh báo từ (%):");

        txtAmount = new JTextField(20);
        txtWarn   = new JTextField(5);
        chkEnable = new JCheckBox("Bật hạn mức chi tiêu tháng này");
        btnSave   = new JButton("Lưu");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(lblTitle)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(lblAmount)
                        .addComponent(lblWarn))
                    .addGroup(layout.createParallelGroup()
                        .addComponent(txtAmount)
                        .addComponent(txtWarn)))
                .addComponent(chkEnable)
                .addComponent(btnSave, GroupLayout.Alignment.TRAILING)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAmount)
                    .addComponent(txtAmount))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWarn)
                    .addComponent(txtWarn))
                .addGap(5)
                .addComponent(chkEnable)
                .addGap(10)
                .addComponent(btnSave)
        );
    }

    private void loadCurrentConfig() {
        if (currentLimit != null && currentLimit.compareTo(BigDecimal.ZERO) > 0) {
            txtAmount.setText(currentLimit.toPlainString());
        }
        txtWarn.setText(Integer.toString(warnPercent));
        chkEnable.setSelected(limitEnabled);
    }

    private void initActions() {
        btnSave.addActionListener(e -> onSave());
    }

    private void onSave() {
        try {
            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty()) {
                throw new IllegalArgumentException("Hạn mức không được để trống.");
            }
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Hạn mức phải > 0.");
            }

            String warnStr = txtWarn.getText().trim();
            int warn = Integer.parseInt(warnStr);
            if (warn < 1 || warn > 100) {
                throw new IllegalArgumentException("Cảnh báo phải trong khoảng 1–100%.");
            }

            currentLimit = amount;
            warnPercent = warn;
            limitEnabled = chkEnable.isSelected();

            JOptionPane.showMessageDialog(this, "Đã lưu hạn mức chi tiêu.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
