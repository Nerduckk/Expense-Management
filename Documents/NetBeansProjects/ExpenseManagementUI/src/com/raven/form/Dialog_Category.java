package com.raven.form;

import com.mycompany.appquanlychitieu.model.Category;
import com.mycompany.appquanlychitieu.model.CategoryType;
import com.mycompany.appquanlychitieu.service.CategoryService;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class Dialog_Category extends JDialog {

    private final CategoryService categoryService;
    private final Category editingCategory;

    private JTextField txtName;
    private JComboBox<CategoryType> comboType;
    private JTextField txtIcon;
    private JTextField txtColor;
    private JTextField txtBudget;
    private JButton btnSave;
    private JButton btnCancel;

    public Dialog_Category(Frame parent,
                           boolean modal,
                           CategoryService service,
                           Category toEdit) {
        super(parent, modal);
        this.categoryService = service;
        this.editingCategory = toEdit;

        initComponents();
        setLocationRelativeTo(parent);

        if (editingCategory != null) {
            loadData();
        }
    }

    private void initComponents() {
        setTitle("Danh mục");
        setSize(420, 260);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel();
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.setLayout(new java.awt.GridLayout(5, 2, 5, 5));

        form.add(new JLabel("Tên danh mục:"));
        txtName = new JTextField();
        form.add(txtName);

        form.add(new JLabel("Loại:"));
        comboType = new JComboBox<>(CategoryType.values());
        form.add(comboType);

        form.add(new JLabel("Icon:"));
        txtIcon = new JTextField();
        form.add(txtIcon);

        form.add(new JLabel("Màu (#RRGGBB, để trống = tự gợi ý):"));
        txtColor = new JTextField();
        form.add(txtColor);

        form.add(new JLabel("Hạn mức (optional):"));
        txtBudget = new JTextField();
        form.add(txtBudget);

        JPanel bottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        bottom.add(btnCancel);
        bottom.add(btnSave);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());
    }

    private void loadData() {
        txtName.setText(editingCategory.getName());
        comboType.setSelectedItem(editingCategory.getType());
        txtIcon.setText(editingCategory.getIcon());
        txtColor.setText(editingCategory.getColor());
        BigDecimal limit = editingCategory.getBudgetLimit();
        txtBudget.setText(limit == null ? "" : limit.toPlainString());
    }

    private void onSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được để trống");
            return;
        }

        CategoryType type = (CategoryType) comboType.getSelectedItem();
        String icon = txtIcon.getText().trim();
        String color = txtColor.getText().trim();

        // 🔥 Nếu người dùng để trống màu → tự gợi ý
        if (color.isEmpty()) {
            color = suggestColor(name, type);
            txtColor.setText(color);   // lưu lại để lần sau mở dialog vẫn thấy
        }

        // Chuẩn hóa: đảm bảo bắt đầu bằng '#'
        if (!color.startsWith("#")) {
            color = "#" + color;
        }

        BigDecimal budget = null;
        String budgetStr = txtBudget.getText().trim();
        if (!budgetStr.isEmpty()) {
            try {
                budget = new BigDecimal(budgetStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Hạn mức không hợp lệ");
                return;
            }
        }

        if (editingCategory == null) {
            Category c = new Category(name, type, icon, color, budget);
            categoryService.add(c);
        } else {
            editingCategory.setName(name);
            editingCategory.setType(type);
            editingCategory.setIcon(icon);
            editingCategory.setColor(color);
            editingCategory.setBudgetLimit(budget);
            categoryService.saveChanges();
        }

        dispose();
    }

    // ================== GỢI Ý MÀU TỰ ĐỘNG ==================

    private String suggestColor(String name, CategoryType type) {
        String n = name.toLowerCase().trim();

        if (type == CategoryType.INCOME) {
            // Thu nhập
            if (n.contains("lương") || n.contains("salary")) {
                return "#C7E8B3";     // xanh lá pastel
            }
            if (n.contains("thưởng") || n.contains("bonus")) {
                return "#BCECE0";     // xanh mint
            }
            return "#D4F1F4";         // thu nhập khác: teal pastel
        } else {
            // Chi tiêu
            if (n.contains("ăn") || n.contains("uống") || n.contains("food")) {
                return "#F8C8C8";     // ăn uống: đỏ pastel
            }
            if (n.contains("mua") || n.contains("sắm") || n.contains("shopping")) {
                return "#FFD8C2";     // mua sắm: cam pastel
            }
            if (n.contains("giải trí") || n.contains("game") || n.contains("phim") 
                    || n.contains("net") || n.contains("cafe")) {
                return "#E4D0FF";     // giải trí: tím pastel
            }
            if (n.contains("y tế") || n.contains("thuốc") || n.contains("bệnh")) {
                return "#D0E8FF";     // y tế: xanh baby blue
            }
            if (n.contains("học") || n.contains("học phí") || n.contains("education")) {
                return "#E4D0FF";     // giáo dục
            }
            if (n.contains("du lịch") || n.contains("travel") || n.contains("tour")) {
                return "#FFF5BA";     // du lịch: vàng pastel
            }
            if (n.contains("hóa đơn") || n.contains("điện") || n.contains("nước")
                    || n.contains("internet") || n.contains("wifi")) {
                return "#E8E8E8";     // hóa đơn: xám pastel
            }

            // Default cho chi tiêu khác
            return "#FDE4E4";         // đỏ pastel rất nhẹ
        }
    }
}
