package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.AbstractTransaction;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CategoryType;
import com.mycompany.appquanlychitieu.model.NormalTransaction;
import com.mycompany.appquanlychitieu.model.TransferTransaction;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.service.TransactionService;
import com.toedter.calendar.JDateChooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Form_Transactions extends JPanel {

    private final TransactionService transactionService;

    // --- filter ---
    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JComboBox<String> comboType;
    private JComboBox<String> comboAccount;
    private JButton btnFilter;

    // --- summary ---
    private JLabel lblIncome;
    private JLabel lblExpense;
    private JLabel lblNet;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnRefresh;
    private JButton btnDelete;

    // list giao dịch đang được hiển thị (phục vụ renderer)
    private List<AbstractTransaction> currentList;

    // =================== CONSTRUCTOR ===================

    public Form_Transactions(TransactionService ts) {
        this.transactionService = ts;
        initComponents();
        loadAccountsToCombo();
        // Lần đầu: load toàn bộ giao dịch
        currentList = transactionService.getAll();
        loadTransactionsToTable();
    }

    public Form_Transactions() {
        this(AppContext.transactionService);
    }

    // =================== INIT COMPONENTS ===================

    private void initComponents() {
        setOpaque(false);

        JLabel lblTitle = new JLabel("Danh sách giao dịch");
        lblTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 16));
        lblTitle.setForeground(new Color(76, 76, 76));

        btnAdd = new JButton("+ Thêm giao dịch");
        btnRefresh = new JButton("↻ Làm mới");
        btnDelete = new JButton("🗑 Xóa");

        // ========== FILTER BAR ==========
        dateFrom = new JDateChooser();
        dateTo = new JDateChooser();

        comboType = new JComboBox<>(new String[]{"Tất cả", "Thu", "Chi"});
        comboAccount = new JComboBox<>();      // sẽ load tên ví sau
        btnFilter = new JButton("Lọc");

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Từ ngày:"));
        filterPanel.add(dateFrom);
        filterPanel.add(new JLabel("Đến ngày:"));
        filterPanel.add(dateTo);
        filterPanel.add(new JLabel("Loại:"));
        filterPanel.add(comboType);
        filterPanel.add(new JLabel("Ví:"));
        filterPanel.add(comboAccount);
        filterPanel.add(btnFilter);

        // ========== TABLE ==========
        String[] columns = {"Mô tả", "Danh mục", "Loại", "Ví", "Số tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);

        // Renderer tô màu cho cột "Danh mục" (index 1)
        table.getColumnModel()
                .getColumn(1)
                .setCellRenderer(new CategoryColorRenderer());

        JScrollPane scroll = new JScrollPane(table);

        // ========== SUMMARY ==========
        lblIncome = new JLabel("Tổng thu: 0");
        lblExpense = new JLabel("Tổng chi: 0");
        lblNet = new JLabel("Chênh lệch: 0");

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.add(lblIncome);
        summaryPanel.add(lblExpense);
        summaryPanel.add(lblNet);

        // ====== Sự kiện nút Thêm ======
        btnAdd.addActionListener(e -> openCreateDialog());

        // ====== Sự kiện nút Refresh ======
        btnRefresh.addActionListener(e -> {
            // clear filter
            dateFrom.setDate(null);
            dateTo.setDate(null);
            comboType.setSelectedIndex(0);
            if (comboAccount.getItemCount() > 0) {
                comboAccount.setSelectedIndex(0);
            }
            // load lại tất cả giao dịch
            currentList = transactionService.getAll();
            loadTransactionsToTable();
        });

        // ====== Nút Lọc ======
        btnFilter.addActionListener(e -> applyFilter());

        // ====== Nút Xóa ======
        btnDelete.addActionListener(e -> deleteSelectedTransaction());

        // ====== Double–click 1 dòng để sửa ======
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    editTransactionAtRow(row);
                }
            }
        });

        // ====== Layout dùng GroupLayout ======
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(filterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                                        .addComponent(summaryPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnRefresh)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnDelete)
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
                                        .addComponent(btnDelete)
                                        .addComponent(btnRefresh))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(filterPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(summaryPanel)
                                .addContainerGap())
        );
    }

    // =================== LOAD & HIỂN THỊ DỮ LIỆU ===================

    private void loadAccountsToCombo() {
        comboAccount.removeAllItems();
        comboAccount.addItem("Tất cả ví");
        for (Account acc : DataStore.accounts) {
            if (acc != null && acc.getName() != null) {
                comboAccount.addItem(acc.getName());
            }
        }
    }

    // Gọi chung: vẽ lại theo currentList hiện tại
    private void loadTransactionsToTable() {
        loadTransactionsToTable(currentList);
    }

    // Vẽ bảng theo list truyền vào
    private void loadTransactionsToTable(List<AbstractTransaction> source) {
        tableModel.setRowCount(0);

        if (source == null || source.isEmpty()) {
            updateSummary(source);
            return;
        }

        DecimalFormat moneyFormat = new DecimalFormat("#,##0");

        for (AbstractTransaction tx : source) {
            if (tx == null) continue;

            String desc = tx.getNote();
            Category cat = null;
            String type;

            if (tx instanceof TransferTransaction) {
                // ===== GIAO DỊCH CHUYỂN TIỀN =====
                if (desc == null || desc.isBlank()) {
                    desc = "Chuyển tiền";
                }
                type = "Chuyển tiền";
            } else {
                // ===== GIAO DỊCH THU / CHI BÌNH THƯỜNG =====
                if (desc == null || desc.isBlank()) {
                    desc = "(Không có mô tả)";
                }
                type = tx.isIncome() ? "Thu nhập" : "Chi tiêu";

                if (tx instanceof NormalTransaction) {
                    cat = ((NormalTransaction) tx).getCategory();
                }
            }

            String catName;
            if (tx instanceof TransferTransaction) {
                catName = "Chuyển tiền";
            } else {
                catName = (cat != null && cat.getName() != null)
                        ? cat.getName()
                        : "(Không phân loại)";
            }

            Account acc = tx.getSourceAccount();
            String accName = (acc != null && acc.getName() != null)
                    ? acc.getName()
                    : "(Không rõ ví)";

            BigDecimal amountBD =
                    tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO;
            String amountStr = moneyFormat.format(amountBD.abs());

            tableModel.addRow(new Object[]{
                    desc,
                    catName,
                    type,
                    accName,
                    amountStr
            });
        }

        // Sau khi vẽ bảng thì cập nhật lại tổng thu/chi/net
        updateSummary(source);
    }

    // =================== FILTER ===================

    private void applyFilter() {
        LocalDate from = null;
        LocalDate to = null;

        if (dateFrom.getDate() != null) {
            from = dateFrom.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        if (dateTo.getDate() != null) {
            to = dateTo.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        // Loại
        String typeStr = (String) comboType.getSelectedItem();
        CategoryType type = null;
        if ("Thu".equals(typeStr)) {
            type = CategoryType.INCOME;
        } else if ("Chi".equals(typeStr)) {
            type = CategoryType.EXPENSE;
        }

        // Ví
        Long accountId = null;
        int accIndex = comboAccount.getSelectedIndex();
        if (accIndex > 0) { // 0 = "Tất cả ví"
            String accName = (String) comboAccount.getSelectedItem();
            for (Account acc : DataStore.accounts) {
                if (acc != null && accName.equals(acc.getName())) {
                    accountId = acc.getId();
                    break;
                }
            }
        }

        List<AbstractTransaction> list =
                transactionService.filter(from, to, type, accountId);

        currentList = list;
        loadTransactionsToTable();
    }

    // =================== DIALOG THÊM / SỬA ===================

    private void openCreateDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_Transaction dlg = new Dialog_Transaction(
                parent,
                true,
                transactionService,
                null
        );
        dlg.setVisible(true);

        // Sau khi thêm xong: áp lại filter hiện tại (hoặc load all nếu không lọc)
        applyFilter();
    }

    private void editTransactionAtRow(int rowIndex) {
        if (rowIndex < 0 || currentList == null || rowIndex >= currentList.size()) {
            return;
        }
        AbstractTransaction tx = currentList.get(rowIndex);
        if (tx == null) return;

        // Chỉ cho phép sửa giao dịch Thu/Chi
        if (!(tx instanceof NormalTransaction)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Giao dịch này là CHUYỂN TIỀN.\n"
                    + "Hiện tại chỉ sửa được giao dịch Thu/Chi.",
                    "Không thể sửa",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_Transaction dlg = new Dialog_Transaction(
                parent,
                true,
                transactionService,
                tx      // tx ở đây chắc chắn là NormalTransaction
        );
        dlg.setVisible(true);

        // Sau khi sửa xong: áp lại filter
        applyFilter();
    }

    // =================== XÓA GIAO DỊCH ===================

    private void deleteSelectedTransaction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn một giao dịch để xoá.",
                    "Chưa chọn dòng",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (currentList == null || row < 0 || row >= currentList.size()) {
            return;
        }

        AbstractTransaction tx = currentList.get(row);
        if (tx == null) return;

        // KHÔNG cho xoá chuyển khoản
        if (tx instanceof TransferTransaction) {
            JOptionPane.showMessageDialog(
                    this,
                    "Giao dịch này là CHUYỂN TIỀN.\n"
                    + "Hiện tại không hỗ trợ xoá chuyển khoản từ màn này.",
                    "Không thể xoá",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xoá giao dịch này?",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Xoá trong service (nhớ đảm bảo TransactionService.deleteTransaction()
        // xử lý hoàn tác số dư cho đúng)
        transactionService.deleteTransaction(tx);

        // Xoá khỏi list đang hiển thị
        currentList.remove(row);

        // Vẽ lại bảng
        loadTransactionsToTable();
    }

    // =================== TÍNH TỔNG THU / CHI / NET ===================

    private void updateSummary(List<AbstractTransaction> list) {
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");

        if (list == null || list.isEmpty()) {
            lblIncome.setText("Tổng thu: 0");
            lblExpense.setText("Tổng chi: 0");
            lblNet.setText("Chênh lệch: 0");
            return;
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (AbstractTransaction t : list) {
            if (t == null) continue;
            if (t.isIncome()) {
                totalIncome = totalIncome.add(
                        t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO
                );
            } else if (t.isExpense()) {
                totalExpense = totalExpense.add(
                        t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO
                );
            }
        }

        BigDecimal net = totalIncome.subtract(totalExpense);

        lblIncome.setText("Tổng thu: " + moneyFormat.format(totalIncome));
        lblExpense.setText("Tổng chi: " + moneyFormat.format(totalExpense));
        lblNet.setText("Chênh lệch: " + moneyFormat.format(net));
    }

    // =================== RENDERER TÔ MÀU DANH MỤC ===================

    private class CategoryColorRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String text = (value == null) ? "(Không phân loại)" : value.toString();
            setText(text);
            setOpaque(true);

            // màu mặc định nếu không chọn
            if (!isSelected) {
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);
            } else {
                c.setForeground(table.getSelectionForeground());
                c.setBackground(table.getSelectionBackground());
            }

            // nếu không có currentList hoặc index lệch thì thôi
            if (currentList == null || row < 0 || row >= currentList.size()) {
                return c;
            }

            // lấy Category từ giao dịch
            AbstractTransaction tx = currentList.get(row);
            Category cat = null;
            if (tx instanceof NormalTransaction) {
                cat = ((NormalTransaction) tx).getCategory();
            }

            if (cat != null && cat.getColor() != null) {
                try {
                    Color base = Color.decode(cat.getColor().trim());
                    double ratio = 0.6;
                    int r = (int) (base.getRed() * (1 - ratio) + 255 * ratio);
                    int g = (int) (base.getGreen() * (1 - ratio) + 255 * ratio);
                    int b = (int) (base.getBlue() * (1 - ratio) + 255 * ratio);

                    Color soft = new Color(r, g, b);

                    if (!isSelected) {
                        c.setBackground(soft);
                        int brightness = (soft.getRed() + soft.getGreen() + soft.getBlue()) / 3;
                        c.setForeground(brightness < 140 ? Color.WHITE : Color.BLACK);
                    }

                } catch (NumberFormatException ex) {
                    // bỏ qua nếu mã màu sai
                }
            }

            return c;
        }
    }
}
