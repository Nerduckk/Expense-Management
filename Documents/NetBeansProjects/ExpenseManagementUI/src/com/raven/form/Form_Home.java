package com.raven.form;
import com.raven.dialog.Message;
import com.raven.main.Main;
import com.raven.model.ModelCard;
import com.raven.model.ModelTransaction;
import com.raven.swing.icon.GoogleMaterialDesignIcons;
import com.raven.swing.icon.IconFontSwing;
import com.raven.swing.noticeboard.ModelNoticeBoard;
import com.raven.swing.table.EventAction;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.mycompany.appquanlychitieu.service.DataStore;       
import com.mycompany.appquanlychitieu.service.TransactionService;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.AbstractTransaction;
import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.model.AbstractTransaction;
import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Debt;
import com.mycompany.appquanlychitieu.model.DebtType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.mycompany.appquanlychitieu.model.NormalTransaction;
import com.mycompany.appquanlychitieu.model.TransferTransaction;
import java.text.DecimalFormat;


public class Form_Home extends javax.swing.JPanel {
    private EventAction tableEvent;
    private final TransactionService transactionService;
    public Form_Home(TransactionService ts) {
        this.transactionService = ts;
        initComponents();
        table1.fixTable(jScrollPane1);
        setOpaque(false);
        initData();
    }
    public Form_Home() {
        this(AppContext.transactionService);
    }

    private void initData() {
        initCardData();
        initNoticeBoard();
        initTableData();
    }
private void reloadTable() {
    DefaultTableModel model = (DefaultTableModel) table1.getModel();
    model.setRowCount(0);
    for (AbstractTransaction t : DataStore.transactions) {
        ModelTransaction row = ModelTransaction.fromTransaction(t);
        model.addRow(row.toRowTable(tableEvent));
    }
}
private void initTableData() {
    tableEvent = new EventAction() {

        @Override
        public void delete(ModelTransaction tx) {
            if (tx.getRawTransaction() instanceof TransferTransaction) {
                showMessage("Không thể xoá giao dịch CHUYỂN KHOẢN.");
                return;
            }

            int opt = JOptionPane.showConfirmDialog(
                    Form_Home.this,
                    "Xóa giao dịch:\n" + tx.getDescription() + " ?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION
            );
            if (opt != JOptionPane.YES_OPTION) {
                return;
            }

            transactionService.deleteTransaction(tx.getRawTransaction());
            DataStore.saveData();
            reloadTable();
            initCardData();   // nếu bạn có hàm này để cập nhật thẻ tổng quan
            showMessage("Đã xoá giao dịch: " + tx.getDescription());
        }

        @Override
        public void update(ModelTransaction tx) {
            // Không cho sửa giao dịch chuyển khoản
            if (tx.getRawTransaction() instanceof TransferTransaction) {
                showMessage("Không thể sửa giao dịch CHUYỂN KHOẢN tại đây.");
                return;
            }

            Dialog_Transaction dlg = new Dialog_Transaction(
                    (Frame) SwingUtilities.getWindowAncestor(Form_Home.this),
                    true,
                    AppContext.transactionService,
                    tx.getRawTransaction()
            );
            dlg.setVisible(true);

            DataStore.saveData();
            reloadTable();
            initCardData();   // nếu có
        }
    };

    // Lần đầu load
    reloadTable();
}
private void initCardData() {
    int month = java.time.LocalDate.now().getMonthValue();
    int year  = java.time.LocalDate.now().getYear();

    // Lấy số liệu từ service
    BigDecimal totalIncomeBD  = transactionService.getTotalIncomeMonth(month, year);
    BigDecimal totalExpenseBD = transactionService.getTotalExpenseMonth(month, year);
    int countTxn              = transactionService.countTransactionsMonth(month, year);

    // Tính tổng số dư từ tất cả ví
    BigDecimal totalBalanceBD = DataStore.accounts.stream()
            .map(Account::getBalance)                       // BigDecimal
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Convert ra double cho ModelCard
    double totalIncome   = totalIncomeBD.doubleValue();
    double totalExpense  = totalExpenseBD.doubleValue();
    double totalBalance  = totalBalanceBD.doubleValue();

    Icon icon1 = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 60,
            new Color(255, 255, 255, 100), new Color(255, 255, 255, 15));
    Icon icon2 = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.MONETIZATION_ON, 60,
            new Color(255, 255, 255, 100), new Color(255, 255, 255, 15));
    Icon icon3 = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SHOPPING_BASKET, 60,
            new Color(255, 255, 255, 100), new Color(255, 255, 255, 15));
    Icon icon4 = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.BUSINESS_CENTER, 60,
            new Color(255, 255, 255, 100), new Color(255, 255, 255, 15));

    card1.setData(new ModelCard("Tổng số dư",      totalBalance,  0, icon1));
    card2.setData(new ModelCard("Thu nhập tháng",  totalIncome,   0, icon2));
    card3.setData(new ModelCard("Chi tiêu tháng",  totalExpense,  0, icon3));
    card4.setData(new ModelCard("Giao dịch tháng", countTxn,      0, icon4));
}
private void initNoticeBoard() {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate to = today.plusDays(7);
    java.text.DecimalFormat fmt = new java.text.DecimalFormat("#,##0");

    // Header ngày hôm nay
    noticeBoard.addDate(today.toString());
    java.time.LocalDate currentDate = today;

    // ====== 1) HẠN MỨC CHI TIÊU THÁNG ======
    try {
        if (Form_BudgetSettings.isLimitEnabled()) {
            java.math.BigDecimal limit = Form_BudgetSettings.getCurrentLimit();
            if (limit != null && limit.compareTo(java.math.BigDecimal.ZERO) > 0) {
                int month = today.getMonthValue();
                int year = today.getYear();

                java.math.BigDecimal spent = transactionService.getTotalExpenseMonth(month, year);
                if (spent == null) spent = java.math.BigDecimal.ZERO;
                if (spent.signum() < 0) spent = spent.negate();

                java.math.BigDecimal percentBD = spent
                        .multiply(java.math.BigDecimal.valueOf(100))
                        .divide(limit, 0, java.math.RoundingMode.HALF_UP);
                int percent = percentBD.intValue();
                int warn = Form_BudgetSettings.getWarnPercent();

                java.awt.Color color;
                if (percent >= 100) {
                    color = new java.awt.Color(220, 53, 69);      // đỏ: vượt hạn mức
                } else if (percent >= warn) {
                    color = new java.awt.Color(255, 193, 7);      // vàng: sắp chạm
                } else {
                    color = new java.awt.Color(40, 167, 69);      // xanh: an toàn
                }

                String title = "Hạn mức chi tiêu tháng " + month + "/" + year;
                String time = "";
                String desc = "Đã chi: " + fmt.format(spent)
                        + " / Hạn mức: " + fmt.format(limit)
                        + " (" + percent + "%)";

                noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                        color,
                        title,
                        time,
                        desc
                ));
            }
        }
    } catch (Exception ex) {
        // nếu có lỗi gì thì bỏ qua phần hạn mức, tránh vỡ cả noticeBoard
        ex.printStackTrace();
    }

    // ====== 2) PHẦN NỢ ======
    java.util.List<com.mycompany.appquanlychitieu.model.Debt> allDebts =
            com.mycompany.appquanlychitieu.service.DataStore.debts;

    java.util.List<com.mycompany.appquanlychitieu.model.Debt> upcoming = java.util.Collections.emptyList();

    if (allDebts == null || allDebts.isEmpty()) {
        // Không có nợ nào
        noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                new java.awt.Color(0, 153, 51),
                "Không có khoản nợ nào",
                "",
                "Hiện tại bạn chưa ghi nhận khoản nợ nào trong hệ thống."
        ));

        noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                new java.awt.Color(0, 153, 51),
                "Không có khoản nợ sắp đến hạn",
                "",
                "Trong 7 ngày tới không có khoản nợ nào đến hạn."
        ));
    } else {
        // Lọc các khoản nợ ACTIVE và đến hạn trong 7 ngày tới
        upcoming =
                allDebts.stream()
                        .filter(java.util.Objects::nonNull)
                        .filter(d -> d.getStatus() == com.mycompany.appquanlychitieu.model.DebtStatus.ACTIVE)
                        .filter(d -> {
                            java.time.LocalDate due = d.getDueDate();
                            return due != null
                                    && !due.isBefore(today)
                                    && !due.isAfter(to);
                        })
                        .sorted(java.util.Comparator.comparing(
                                com.mycompany.appquanlychitieu.model.Debt::getDueDate
                        ))
                        .toList();

        if (upcoming.isEmpty()) {
            noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                    new java.awt.Color(0, 153, 51),
                    "Không có khoản nợ sắp đến hạn",
                    "",
                    "Trong 7 ngày tới không có khoản nợ nào đến hạn."
            ));
        } else {
            for (com.mycompany.appquanlychitieu.model.Debt d : upcoming) {
                java.time.LocalDate due = d.getDueDate();
                if (due == null) continue;

                if (!due.equals(currentDate)) {
                    currentDate = due;
                    noticeBoard.addDate(due.toString());
                }

                String title;
                if (d.getType() == com.mycompany.appquanlychitieu.model.DebtType.BORROWING) {
                    title = "Hạn trả nợ cho " + d.getPersonName();
                } else {
                    title = "Thu nợ từ " + d.getPersonName();
                }

                String time = "";
                String desc = "Số tiền còn lại: " + fmt.format(d.getRemainingAmount())
                        + " / Gốc: " + fmt.format(d.getPrincipalAmount());

                noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                        new java.awt.Color(238, 46, 57),
                        title,
                        time,
                        desc
                ));
            }
        }
    }

    // ====== 3) PHẦN GIAO DỊCH ĐỊNH KỲ ======
    java.util.List<com.mycompany.appquanlychitieu.model.RecurringSchedule> recList =
            com.mycompany.appquanlychitieu.service.DataStore.recurringSchedules;

    if (recList != null && !recList.isEmpty()) {
        java.util.List<com.mycompany.appquanlychitieu.model.RecurringSchedule> upcomingRec =
                recList.stream()
                        .filter(java.util.Objects::nonNull)
                        .filter(rs -> {
                            java.time.LocalDate next = rs.getNextDueDate();
                            return next != null
                                    && !next.isBefore(today)
                                    && !next.isAfter(to)
                                    && rs.isActiveOn(next);
                        })
                        .sorted(java.util.Comparator.comparing(
                                com.mycompany.appquanlychitieu.model.RecurringSchedule::getNextDueDate
                        ))
                        .toList();

        for (com.mycompany.appquanlychitieu.model.RecurringSchedule rs : upcomingRec) {
            java.time.LocalDate due = rs.getNextDueDate();
            if (due == null) continue;

            if (!due.equals(currentDate)) {
                currentDate = due;
                noticeBoard.addDate(due.toString());
            }

            String title = "Giao dịch định kỳ: " + rs.getName();
            String time = "";
            String amountStr = (rs.getAmount() == null)
                    ? "0"
                    : fmt.format(rs.getAmount());

            String desc = "Số tiền: " + amountStr;
            if (rs.getAccount() != null) {
                desc += " | Tài khoản: " + rs.getAccount().getName();
            }

            noticeBoard.addNoticeBoard(new ModelNoticeBoard(
                    new java.awt.Color(0, 102, 204),
                    title,
                    time,
                    desc
            ));
        }
    }

    noticeBoard.scrollToTop();
}
    private void showMessage(String message) {
        Message obj = new Message(Main.getFrames()[0], true);
        obj.showMessage(message);
    }
private void addSpendingLimitNotice(java.time.LocalDate today) {
    // Nếu chưa bật hạn mức thì thôi
    if (!Form_BudgetSettings.isLimitEnabled()) {
        return;
    }

    BigDecimal limit = Form_BudgetSettings.getCurrentLimit();
    if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
        return;
    }

    int month = today.getMonthValue();
    int year  = today.getYear();

    // Dùng service có sẵn để lấy tổng chi tháng
    BigDecimal spent = AppContext.transactionService
            .getTotalExpenseMonth(month, year);
    if (spent == null) spent = BigDecimal.ZERO;

    // Đảm bảo dương
    if (spent.signum() < 0) {
        spent = spent.negate();
    }

    if (limit.compareTo(BigDecimal.ZERO) == 0) return;

    BigDecimal percent = spent
            .multiply(BigDecimal.valueOf(100))
            .divide(limit, 0, java.math.RoundingMode.HALF_UP);

    int warn = Form_BudgetSettings.getWarnPercent();

    DecimalFormat df = new DecimalFormat("#,##0");
    String title = "Hạn mức chi tiêu tháng " + month + "/" + year;
    String time  = "";
    String desc  = "Đã chi: " + df.format(spent) + " / Hạn mức: "
                 + df.format(limit) + " (" + percent.toPlainString() + "%)";

    Color color;
    int p = percent.intValue();
    if (p >= 100) {
        color = new Color(220, 53, 69);        // đỏ: vượt hạn mức
    } else if (p >= warn) {
        color = new Color(255, 193, 7);        // vàng: sắp chạm
    } else {
        color = new Color(40, 167, 69);        // xanh: an toàn
    }

    // Ở đây mình KHÔNG addDate nữa, để dùng chung date header với phần nợ
    noticeBoard.addNoticeBoard(new ModelNoticeBoard(
            color,
            title,
            time,
            desc
    ));
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card1 = new com.raven.component.Card();
        jLabel1 = new javax.swing.JLabel();
        card2 = new com.raven.component.Card();
        card3 = new com.raven.component.Card();
        card4 = new com.raven.component.Card();
        panelTransparent1 = new com.raven.swing.PanelTransparent();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.table.Table();
        jLabel5 = new javax.swing.JLabel();
        panelTransparent2 = new com.raven.swing.PanelTransparent();
        noticeBoard = new com.raven.swing.noticeboard.NoticeBoard();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        card1.setColorGradient(new java.awt.Color(211, 28, 215));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(4, 72, 210));
        jLabel1.setText("Dashboard / Quản lý chi tiêu");

        card2.setBackground(new java.awt.Color(10, 30, 214));
        card2.setColorGradient(new java.awt.Color(72, 111, 252));

        card3.setBackground(new java.awt.Color(194, 85, 1));
        card3.setColorGradient(new java.awt.Color(255, 212, 99));

        card4.setBackground(new java.awt.Color(60, 195, 0));
        card4.setColorGradient(new java.awt.Color(208, 255, 90));

        panelTransparent1.setTransparent(0.5F);

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mô tả", "Loại", "Ví", "Số tiền", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setPreferredWidth(150);
        }

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(76, 76, 76));
        jLabel5.setText("Giao dịch gần đây");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        javax.swing.GroupLayout panelTransparent1Layout = new javax.swing.GroupLayout(panelTransparent1);
        panelTransparent1.setLayout(panelTransparent1Layout);
        panelTransparent1Layout.setHorizontalGroup(
            panelTransparent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransparent1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTransparent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTransparent1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        panelTransparent1Layout.setVerticalGroup(
            panelTransparent1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransparent1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelTransparent2.setTransparent(0.5F);

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(105, 105, 105));
        jLabel3.setText("Gợi ý chi tiêu & hóa đơn sắp tới");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(76, 76, 76));
        jLabel2.setText("Nhắc nhở tài chính");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        jLabel4.setOpaque(true);

        javax.swing.GroupLayout panelTransparent2Layout = new javax.swing.GroupLayout(panelTransparent2);
        panelTransparent2.setLayout(panelTransparent2Layout);
        panelTransparent2Layout.setHorizontalGroup(
            panelTransparent2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransparent2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTransparent2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(noticeBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTransparent2Layout.createSequentialGroup()
                        .addGroup(panelTransparent2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 257, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelTransparent2Layout.setVerticalGroup(
            panelTransparent2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransparent2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(15, 15, 15)
                .addComponent(jLabel3)
                .addGap(9, 9, 9)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(noticeBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(card1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(card2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(card4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(panelTransparent1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelTransparent2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTransparent2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTransparent1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card1;
    private com.raven.component.Card card2;
    private com.raven.component.Card card3;
    private com.raven.component.Card card4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.noticeboard.NoticeBoard noticeBoard;
    private com.raven.swing.PanelTransparent panelTransparent1;
    private com.raven.swing.PanelTransparent panelTransparent2;
    private com.raven.swing.table.Table table1;
    // End of variables declaration//GEN-END:variables
}
