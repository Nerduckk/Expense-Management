package com.mycompany.appquanlychitieu.service;

import com.mycompany.appquanlychitieu.model.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final String FILE_ACCOUNTS = "accounts.dat";
    private static final String FILE_CATEGORIES = "categories.dat";
    private static final String FILE_TRANSACTIONS = "transactions.dat";
    private static final String FILE_USERS = "users.dat";
    private static final String FILE_DEBTS = "debts.dat";
    public static List<User> users;
    public static List<Account> accounts;
    public static List<Category> categories;
    public static List<AbstractTransaction> transactions;
    public static List<Debt> debts;


    public static void loadData() {
    // ===== USERS =====
    users = FileHelper.loadFromFile(FILE_USERS);
    if (users == null) {
        users = new java.util.ArrayList<>();
    }

    // ===== ACCOUNTS =====
    accounts = FileHelper.loadFromFile(FILE_ACCOUNTS);
    if (accounts == null) {
        accounts = new java.util.ArrayList<>();
    }

    // ===== CATEGORIES =====
    categories = FileHelper.loadFromFile(FILE_CATEGORIES);
    if (categories == null) {
        categories = new java.util.ArrayList<>();
    }

    // ===== TRANSACTIONS =====
    transactions = FileHelper.loadFromFile(FILE_TRANSACTIONS);
    if (transactions == null) {
        transactions = new java.util.ArrayList<>();
    }
    debts = FileHelper.loadFromFile(FILE_DEBTS);
    if (debts == null) {
    debts = new ArrayList<>();
    }


    // === 1. Tạo user mặc định nếu chưa có ===
    if (users.isEmpty()) {
        User admin = new User(
                1L,
                "Admin",
                "admin@example.com",
                "VND"
        );
        admin.setPasswordHash("123456");    // mật khẩu đăng nhập

        users.add(admin);
        FileHelper.saveToFile(users, FILE_USERS);
    }

    // === 2. Tạo account mặc định nếu chưa có ===
    if (accounts.isEmpty()) {
        accounts.add(new Account(1L, "Tiền mặt",
                new java.math.BigDecimal("2000000"), "VND"));
        accounts.add(new Account(2L, "Vietcombank",
                new java.math.BigDecimal("5000000"), "VND"));
        accounts.add(new Account(3L, "Ví Momo",
                new java.math.BigDecimal("300000"), "VND"));

        FileHelper.saveToFile(accounts, FILE_ACCOUNTS);
    }

    // === 3. Đảm bảo luôn có INCOME + EXPENSE ===
    boolean hasIncome = categories.stream()
            .anyMatch(c -> c.getType() == CategoryType.INCOME);
    boolean hasExpense = categories.stream()
            .anyMatch(c -> c.getType() == CategoryType.EXPENSE);

    boolean changed = false;

    if (!hasIncome) {
        Category luong = new Category(
                "Lương",
                CategoryType.INCOME,
                "money",
                "#4CAF50",
                null
        );
        categories.add(luong);
        changed = true;
    }

    if (!hasExpense) {
        Category anUong = new Category(
                "Ăn uống",
                CategoryType.EXPENSE,
                "restaurant",
                "#F44336",
                null
        );
        categories.add(anUong);
        changed = true;
    }

    if (changed) {
        FileHelper.saveToFile(categories, FILE_CATEGORIES);
    }
}
    public static void saveData() {
        FileHelper.saveToFile(accounts, FILE_ACCOUNTS);
        FileHelper.saveToFile(categories, FILE_CATEGORIES);
        FileHelper.saveToFile(transactions, FILE_TRANSACTIONS);
        FileHelper.saveToFile(users, FILE_USERS);
        FileHelper.saveToFile(users, FILE_USERS);
        FileHelper.saveToFile(debts, FILE_DEBTS); 
    }
}