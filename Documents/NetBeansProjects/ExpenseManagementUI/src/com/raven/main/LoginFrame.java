package com.raven.main;
import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.User;
import com.mycompany.appquanlychitieu.service.AuthService;
import com.raven.main.Main;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    private final AuthService authService = new AuthService();

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập - Quản lý chi tiêu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);
        setResizable(false);

        // ====== Tạo component ======
        JLabel lbEmail = new JLabel("Email:");
        JLabel lbPassword = new JLabel("Mật khẩu:");

        txtEmail = new JTextField(20);
        txtPassword = new JPasswordField(20);

        btnLogin = new JButton("Đăng nhập");
        btnExit = new JButton("Thoát");

        // ====== Layout đơn giản ======
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lbEmail, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(txtEmail, gbc);

        // Dòng 2: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lbPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtPassword, gbc);

        // Dòng 3: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);

        // ====== Sự kiện ======
        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));

        // Enter để login
        txtPassword.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ email và mật khẩu",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = authService.login(email, password);
            if (user != null) {
                AppContext.setCurrentUser(user);
                Main mainFrame = new Main();
                mainFrame.setVisible(true);

                // Đóng màn login
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Sai email hoặc mật khẩu",
                        "Đăng nhập thất bại",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra khi đăng nhập:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ====== Điểm vào chương trình ======
    public static void main(String[] args) {
        AppContext.init();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
