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
}
