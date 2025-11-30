package com.raven.model;

import com.mycompany.appquanlychitieu.model.AbstractTransaction;
import com.mycompany.appquanlychitieu.model.Account;
import com.raven.swing.table.EventAction;
import com.raven.swing.table.ModelAction;
import com.raven.swing.table.ModelProfile;

import java.text.DecimalFormat;
import javax.swing.Icon;

public class ModelTransaction {

    private Icon icon;
    // Mô tả giao dịch (note hoặc name)
    private String description;
    // Loại: Thu nhập / Chi tiêu / Khác
    private String type;
    // Tên ví / tài khoản
    private String walletName;
    // Số tiền
    private double amount;
    // Giao dịch thật ở backend
    private AbstractTransaction rawTransaction;

    public ModelTransaction() {
    }

    public ModelTransaction(Icon icon,
                            String description,
                            String type,
                            String walletName,
                            double amount,
                            AbstractTransaction rawTransaction) {
        this.icon = icon;
        this.description = description;
        this.type = type;
        this.walletName = walletName;
        this.amount = amount;
        this.rawTransaction = rawTransaction;
    }

    // ==== getter / setter cơ bản ====
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // ===== phần QUAN TRỌNG cho backend =====
    public AbstractTransaction getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTransaction(AbstractTransaction rawTransaction) {
        this.rawTransaction = rawTransaction;
    }

    // Tạo ModelTransaction từ AbstractTransaction backend
    public static ModelTransaction fromTransaction(AbstractTransaction tx) {
        Icon icon = null;   // nếu sau này muốn custom icon theo loại thì set ở đây

        String desc = tx.getNote();
        if (desc == null || desc.isBlank()) {
            desc = tx.getName();   // fallback sang name nếu note trống
        }

        String type;
        if (tx.isIncome()) {
            type = "Thu nhập";
        } else if (tx.isExpense()) {
            type = "Chi tiêu";
        } else {
            type = "Khác";
        }

        Account acc = tx.getSourceAccount();
        String wallet = (acc != null) ? acc.getName() : "";

        double amt = (tx.getAmount() != null) ? tx.getAmount().doubleValue() : 0.0;

        return new ModelTransaction(icon, desc, type, wallet, amt, tx);
    }

    // Dữ liệu 1 dòng cho Table
    public Object[] toRowTable(EventAction event) {
        DecimalFormat df = new DecimalFormat("#,##0.##");

        return new Object[]{
                new ModelProfile(icon, description),
                type,
                walletName,
                df.format(amount),
                new ModelAction(this, event)
        };
    }
}
