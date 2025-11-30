package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CategoryType;
import com.mycompany.appquanlychitieu.service.CategoryService;

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

public class Form_Categories extends JPanel {

    private final CategoryService categoryService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    public Form_Categories(CategoryService cs) {
        this.categoryService = cs;
        initComponents();
        loadCategoriesToTable();
    }

    public Form_Categories() {
        this(AppContext.categoryService);
    }

    private void initComponents() {
        setOpaque(false);

        JLabel lblTitle = new JLabel("Danh mục");
        lblTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 16));
        lblTitle.setForeground(new Color(76, 76, 76));

        btnAdd = new JButton("+ Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("↻ Làm mới");

        String[] columns = {"Tên", "Loại", "Icon", "Màu", "Hạn mức"};
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
        btnRefresh.addActionListener(e -> loadCategoriesToTable());

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
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblTitle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void loadCategoriesToTable() {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,##0.##");

        List<Category> list = categoryService.getAll();
        for (Category c : list) {
            if (c == null) continue;
            String type = c.getType() == CategoryType.INCOME ? "Thu nhập" : "Chi tiêu";
            BigDecimal limit = c.getBudgetLimit();
            String limitStr = limit == null ? "" : df.format(limit);
            tableModel.addRow(new Object[]{
                    c.getName(),
                    type,
                    c.getIcon(),
                    c.getColor(),
                    limitStr
            });
        }
    }

    private void openDialog(Category cat) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        Dialog_Category dlg = new Dialog_Category(parent, true, categoryService, cat);
        dlg.setVisible(true);
        loadCategoriesToTable();
    }

    private Category getSelectedCategory() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        List<Category> list = categoryService.getAll();
        if (row < 0 || row >= list.size()) return null;
        return list.get(row);
    }

    private void editSelected() {
        Category cat = getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa");
            return;
        }
        openDialog(cat);
    }

    private void deleteSelected() {
        Category cat = getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Xóa danh mục \"" + cat.getName() + "\"?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            categoryService.delete(cat);
            loadCategoriesToTable();
        }
    }
}
