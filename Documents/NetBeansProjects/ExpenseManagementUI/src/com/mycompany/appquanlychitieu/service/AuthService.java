package com.mycompany.appquanlychitieu.service;

import com.mycompany.appquanlychitieu.model.User;
import java.util.Optional;

public class AuthService {

    public User login(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            return null;
        }

        // Phòng trường hợp DataStore.users chưa được khởi tạo
        if (DataStore.users == null) {
            DataStore.loadData();
        }

        Optional<User> opt = DataStore.users.stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();

        if (opt.isPresent()) {
            User u = opt.get();
            // Ở đây mình so sánh trực tiếp, sau này thích thì đổi sang hash/checkPassword
            if (rawPassword.equals(u.getPasswordHash())) {
                return u;
            }
        }
        return null;
    }
    public void updateProfile(User user, String newName, String newEmail) {
    if (user == null) {
        throw new IllegalArgumentException("Chưa đăng nhập.");
    }
    if (newName == null || newName.trim().isEmpty()) {
        throw new IllegalArgumentException("Tên không được để trống.");
    }
    if (newEmail == null || newEmail.trim().isEmpty()) {
        throw new IllegalArgumentException("Email không được để trống.");
    }

    String trimmedName  = newName.trim();
    String trimmedEmail = newEmail.trim();

    // Dùng for thường để kiểm tra trùng email
    for (User u : DataStore.users) {
        if (u == null) continue;
        if (u.getId() == null) continue;

        // bỏ qua chính user hiện tại
        if (u.getId().equals(user.getId())) {
            continue;
        }

        if (trimmedEmail.equalsIgnoreCase(u.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng bởi tài khoản khác.");
        }
    }

    user.setName(trimmedName);
    user.setEmail(trimmedEmail);
    DataStore.saveData();
}
    public void changePassword(User user, String currentPassword, String newPassword) {
        if (user == null) {
            throw new IllegalArgumentException("Chưa đăng nhập.");
        }
        if (currentPassword == null || !currentPassword.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự.");
        }

        user.setPasswordHash(newPassword);    // nếu tên setter khác, sửa lại cho khớp
        DataStore.saveData();
    }

}
