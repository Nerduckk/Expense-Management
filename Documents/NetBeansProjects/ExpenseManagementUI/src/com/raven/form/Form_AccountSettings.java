package com.raven.form;

import com.mycompany.appquanlychitieu.AppContext;
import com.mycompany.appquanlychitieu.model.User;

import javax.swing.*;
import java.awt.*;

public class Form_AccountSettings extends JPanel {

    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtCurrentPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JButton btnSaveProfile;
    private JButton btnChangePassword;
    private JButton btnChangeAvatar;

    public Form_AccountSettings() {
        initComponents();
        loadCurrentUser();
        initActions();
    }

    private void initComponents() {
    setOpaque(false);

    JLabel lblTitleProfile = new JLabel("Thông tin tài khoản");
    btnChangeAvatar = new JButton("Đổi ảnh đại diện");
    lblTitleProfile.setFont(lblTitleProfile.getFont().deriveFont(Font.BOLD, 16f));

    JLabel lblName = new JLabel("Tên hiển thị:");
    JLabel lblEmail = new JLabel("Email:");

    txtName = new JTextField(25);
    txtEmail = new JTextField(25);

    btnSaveProfile = new JButton("Lưu thông tin");

    JLabel lblTitlePassword = new JLabel("Đổi mật khẩu");
    lblTitlePassword.setFont(lblTitlePassword.getFont().deriveFont(Font.BOLD, 16f));

    JLabel lblCurrentPass = new JLabel("Mật khẩu hiện tại:");
    JLabel lblNewPass = new JLabel("Mật khẩu mới:");
    JLabel lblConfirmPass = new JLabel("Xác nhận mật khẩu:");

    txtCurrentPass = new JPasswordField(25);
    txtNewPass = new JPasswordField(25);
    txtConfirmPass = new JPasswordField(25);

    btnChangePassword = new JButton("Đổi mật khẩu");

    // ❗ Tạo 1 separator dùng chung cho cả 2 group
    JSeparator sep = new JSeparator();

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    // HORIZONTAL
    layout.setHorizontalGroup(
        layout.createParallelGroup()
            .addComponent(lblTitleProfile)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(lblName)
                    .addComponent(lblEmail))
                .addGroup(layout.createParallelGroup()
                    .addComponent(txtName)
                    .addComponent(txtEmail)))
            .addComponent(btnSaveProfile, GroupLayout.Alignment.TRAILING)
            .addGap(20)
            .addComponent(sep)   // dùng cùng 1 biến sep
            .addComponent(lblTitlePassword)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCurrentPass)
                    .addComponent(lblNewPass)
                    .addComponent(lblConfirmPass))
                .addGroup(layout.createParallelGroup()
                    .addComponent(txtCurrentPass)
                    .addComponent(txtNewPass)
                    .addComponent(txtConfirmPass)))
            .addComponent(btnChangePassword, GroupLayout.Alignment.TRAILING)
    );

    // VERTICAL
    layout.setVerticalGroup(
        layout.createSequentialGroup()
            .addComponent(lblTitleProfile)
            .addGap(10)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblName)
                .addComponent(txtName))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblEmail)
                .addComponent(txtEmail))
            .addGap(10)
            .addComponent(btnSaveProfile)
            .addGap(20)
            .addComponent(sep, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)  // vẫn là sep
            .addGap(10)
            .addComponent(lblTitlePassword)
            .addGap(10)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblCurrentPass)
                .addComponent(txtCurrentPass))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblNewPass)
                .addComponent(txtNewPass))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblConfirmPass)
                .addComponent(txtConfirmPass))
            .addGap(10)
            .addComponent(btnChangePassword)
    );
}


    private void loadCurrentUser() {
        User u = AppContext.getCurrentUser();
        if (u != null) {
            txtName.setText(u.getName());
            txtEmail.setText(u.getEmail());
        }
    }

    private void initActions() {
        btnSaveProfile.addActionListener(e -> onSaveProfile());
        btnChangePassword.addActionListener(e -> onChangePassword());
    }

    private void onSaveProfile() {
        User u = AppContext.getCurrentUser();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();

        try {
            AppContext.authService.updateProfile(u, name, email);

            // Cập nhật lại header
            if (AppContext.getHeader() != null) {
                AppContext.getHeader().setUserName(u.getName());
            }

            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onChangePassword() {
        User u = AppContext.getCurrentUser();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String current = new String(txtCurrentPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            AppContext.authService.changePassword(u, current, newPass);

            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công.");
            txtCurrentPass.setText("");
            txtNewPass.setText("");
            txtConfirmPass.setText("");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void onChangeAvatar() {
    User u = AppContext.getCurrentUser();
    if (u == null) {
        JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        String path = chooser.getSelectedFile().getAbsolutePath();

        // Nếu User có field avatarPath thì lưu lại:
        // u.setAvatarPath(path);
        // DataStore.saveData();

        if (AppContext.getHeader() != null) {
            AppContext.getHeader().setUserAvatar(path);
        }
    }
}
}
