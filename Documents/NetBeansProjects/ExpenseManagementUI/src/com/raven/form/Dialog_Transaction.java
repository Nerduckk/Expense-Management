package com.raven.form;

import com.mycompany.appquanlychitieu.model.AbstractTransaction;
import com.mycompany.appquanlychitieu.model.Account;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CategoryType;
import com.mycompany.appquanlychitieu.model.NormalTransaction;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.service.TransactionService;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Dialog_Transaction extends javax.swing.JDialog {

    private final TransactionService transactionService;
    /** Giao dịch đang sửa (null nếu là thêm mới) */
    private AbstractTransaction editingTransaction;

    private JTextField txtDescription;
    private JTextField txtAmount;
    private JComboBox<String> comboType;
    private JComboBox<Account> comboAccount;
    private JComboBox<Category> comboCategory;

    public Dialog_Transaction(Frame parent,
                              boolean modal,
                              TransactionService service,
                              AbstractTransaction toEdit) {
        super(parent, modal);
        this.transactionService = service;
        this.editingTransaction = toEdit;

        initComponents();
        setLocationRelativeTo(parent);
        initComboBoxes();

        if (editingTransaction != null) {
            loadEditingData(editingTransaction);
        }
    }

    /** Khởi tạo UI đơn giản cho dialog */
    private void initComponents() {
        setTitle("Thêm / sửa giao dịch");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(new java.awt.GridLayout(0, 2, 8, 8));

        // Mô tả
        mainPanel.add(new JLabel("Mô tả:"));
        txtDescription = new JTextField();
        mainPanel.add(txtDescription);

        // Số tiền
        mainPanel.add(new JLabel("Số tiền:"));
        txtAmount = new JTextField();
        mainPanel.add(txtAmount);

        // Loại
        mainPanel.add(new JLabel("Loại:"));
        comboType = new JComboBox<>(new String[]{"Thu nhập", "Chi tiêu"});
        mainPanel.add(comboType);

        // Danh mục
        mainPanel.add(new JLabel("Danh mục:"));
        comboCategory = new JComboBox<>();
        mainPanel.add(comboCategory);

        // Ví
        mainPanel.add(new JLabel("Ví:"));
        comboAccount = new JComboBox<>();
        mainPanel.add(comboAccount);

        // Nút bấm
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private void initComboBoxes() {
        // Load tài khoản
        comboAccount.removeAllItems();
        for (Account acc : DataStore.accounts) {
            comboAccount.addItem(acc);
        }

        // Load danh mục cho loại mặc định (Thu nhập)
        reloadCategoriesForCurrentType();

        // Khi đổi loại giao dịch thì reload lại danh mục
        comboType.addActionListener(e -> reloadCategoriesForCurrentType());
    }

    private void reloadCategoriesForCurrentType() {
        comboCategory.removeAllItems();
        boolean isIncome = comboType.getSelectedIndex() == 0;
        CategoryType type = isIncome ? CategoryType.INCOME : CategoryType.EXPENSE;
        for (Category cat : DataStore.categories) {
            if (cat.getType() == type) {
                comboCategory.addItem(cat);
            }
        }
    }

    private void loadEditingData(AbstractTransaction tx) {
        this.editingTransaction = tx;
        txtDescription.setText(tx.getNote() == null ? "" : tx.getNote());
        BigDecimal amount = tx.getAmount() == null ? BigDecimal.ZERO : tx.getAmount();
        boolean income = tx.isIncome();
        if (!income) {
            amount = amount.abs();
        }
        comboType.setSelectedIndex(income ? 0 : 1);
        txtAmount.setText(amount.toPlainString());
        Account acc = tx.getSourceAccount();
        if (acc != null) {
            comboAccount.setSelectedItem(acc);
        }

        // Chọn đúng danh mục nếu là NormalTransaction
        if (tx instanceof NormalTransaction) {
            NormalTransaction normal = (NormalTransaction) tx;
            Category cat = normal.getCategory();   // đảm bảo trong NormalTransaction có getCategory()
            if (cat != null) {
                comboCategory.setSelectedItem(cat);
            }
        }
    }

    /** Xử lý nút Lưu */
    private void onSave() {
        String desc = txtDescription.getText().trim();
        String amountText = txtAmount.getText().trim();
        Account account = (Account) comboAccount.getSelectedItem();
        Category category = (Category) comboCategory.getSelectedItem();

        if (desc.isEmpty() || amountText.isEmpty() || account == null || category == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin và chọn ví, danh mục.",
                    "Thiếu dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Số tiền không hợp lệ.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isIncome = comboType.getSelectedIndex() == 0;
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Số tiền phải lớn hơn 0.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đảm bảo danh mục đúng loại giao dịch
        CategoryType cType = isIncome ? CategoryType.INCOME : CategoryType.EXPENSE;
        if (category.getType() != cType) {
            JOptionPane.showMessageDialog(this,
                    "Danh mục không phù hợp với loại giao dịch.",
                    "Sai danh mục",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (editingTransaction == null) {
            // Thêm mới
            long newId = DataStore.transactions.stream()
                    .mapToLong(t -> t.getId() == null ? 0L : t.getId())
                    .max()
                    .orElse(0L) + 1;

            NormalTransaction tx = new NormalTransaction(
                    newId,
                    amount,
                    LocalDate.now(),
                    account,
                    category
            );
            tx.setNote(desc);

            transactionService.addTransaction(tx);
        } else {
            long id = editingTransaction.getId() == null
                    ? DataStore.transactions.stream()
                        .mapToLong(t -> t.getId() == null ? 0L : t.getId())
                        .max()
                        .orElse(0L) + 1
                    : editingTransaction.getId();

            transactionService.deleteTransaction(editingTransaction);

            NormalTransaction tx = new NormalTransaction(
                    id,
                    amount,
                    LocalDate.now(),
                    account,
                    category
            );
            tx.setNote(desc);

            transactionService.addTransaction(tx);
        }

        JOptionPane.showMessageDialog(this, "Đã lưu giao dịch.");
        dispose();
    }
}
