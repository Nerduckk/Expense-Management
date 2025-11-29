package com.mycompany.appquanlychitieu;

import com.mycompany.appquanlychitieu.model.*;
import com.mycompany.appquanlychitieu.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appquanlychitieu {

    public static void main(String[] args) {
        System.out.println("====== BAT DAU HE THONG QUAN LY CHI TIEU (CO LUU FILE) ======\n");

        // 1. KHOI DONG: Load du lieu tu file len RAM
        DataStore.loadData();
        TransactionService service = new TransactionService();

        // ==================================================================
        // PHAN 1: KHOI TAO DU LIEU
        // ==================================================================
        System.out.println("--- [1] Khoi tao du lieu ---");

        Account bankAcc = null; // Khai bao null de tranh loi khoi tao
        Account cashAcc = null;
        Category catSalary, catFood, catDebtLending, catDebtCollection;

        // KIEM TRA: Neu chua co tai khoan nao thi tao moi (Lan chay dau tien)
        if (DataStore.accounts.isEmpty()) {
            System.out.println(">> Phat hien lan chay dau tien. Tao du lieu mau...");
            
            // Tao User (Luu y: Constructor phai khop voi User.java)
            User user = new User(1L, "Duck Developer", "duck@code.com", "VND"); 
            // Neu User co password thi them tham so vao dong tren
            
            bankAcc = new Account(1L, "MB Bank", new BigDecimal("10000000"), "VND");
            cashAcc = new Account(2L, "Vi Tien Mat", new BigDecimal("500000"), "VND");
            DataStore.accounts.add(bankAcc);
            DataStore.accounts.add(cashAcc);

            catSalary = new Category(1L, "Luong", CategoryType.INCOME);
            catFood = new Category(2L, "An uong", CategoryType.EXPENSE);
            catDebtLending = new Category(3L, "Cho vay", CategoryType.EXPENSE);
            catDebtCollection = new Category(4L, "Thu hoi no", CategoryType.INCOME);
            
            DataStore.categories.add(catSalary);
            DataStore.categories.add(catFood);
            DataStore.categories.add(catDebtLending);
            DataStore.categories.add(catDebtCollection);

            // QUAN TRONG: Luu ngay xuong file
            DataStore.saveData();
        } else {
            System.out.println(">> Da tim thay du lieu cu tu File.");
            // Lay du lieu an toan (kiem tra size truoc khi get)
            if (DataStore.accounts.size() >= 1) bankAcc = DataStore.accounts.get(0);
            if (DataStore.accounts.size() >= 2) cashAcc = DataStore.accounts.get(1);
            
            // Lay category (Gia dinh rang file luon du category co ban)
            catSalary = DataStore.categories.isEmpty() ? new Category(0L, "Ao", CategoryType.INCOME) : DataStore.categories.get(0);
            catFood = DataStore.categories.size() > 1 ? DataStore.categories.get(1) : catSalary;
            catDebtLending = DataStore.categories.size() > 2 ? DataStore.categories.get(2) : catSalary;
            catDebtCollection = DataStore.categories.size() > 3 ? DataStore.categories.get(3) : catSalary;
        }

        // Neu khong lay duoc tai khoan (do file cu bi loi), thi dung chuong trinh
        if (bankAcc == null || cashAcc == null) {
            System.err.println("LOI: Du lieu file khong du tai khoan de chay demo. Hay xoa file .dat va chay lai.");
            return;
        }

        printBalance(bankAcc);
        printBalance(cashAcc);

        // ==================================================================
        // PHAN 2: CAC GIAO DICH THUONG
        // ==================================================================
        
        // --- BUOC 2: Nhan Luong ---
        System.out.println("\n--- [2] Giao dich: Nhan luong ---");
        NormalTransaction salaryTxn = new NormalTransaction(
            System.currentTimeMillis(), new BigDecimal("15000000"), LocalDate.now(), bankAcc, catSalary
        );
        salaryTxn.setNote("Luong Thang 10");
        service.addTransaction(salaryTxn); // Goi Service de luu file

        // --- BUOC 3: Chi Tieu ---
        System.out.println("\n--- [3] Giao dich: Di an nha hang ---");
        NormalTransaction foodTxn = new NormalTransaction(
            System.currentTimeMillis() + 1, new BigDecimal("500000"), LocalDate.now(), cashAcc, catFood
        );
        foodTxn.setNote("An toi");
        service.addTransaction(foodTxn); 

        // --- BUOC 4: Chuyen khoan ---
        System.out.println("\n--- [4] Giao dich: Rut tien ve Vi ---");
        TransferTransaction transferTxn = new TransferTransaction(
            System.currentTimeMillis() + 2, new BigDecimal("2000000"), LocalDate.now(), bankAcc, cashAcc, new BigDecimal("1100")
        );
        transferTxn.setNote("Rut tien tieu vat");
        service.addTransaction(transferTxn);

        printBalance(bankAcc);
        printBalance(cashAcc);

        // ==================================================================
        // PHAN 3: QUAN LY NO
        // ==================================================================
        System.out.println("\n--- [5] Quan ly No ---");
        
        Debt loanDebt = new Debt(1L, "No cua Hung", DebtType.LENDING, new BigDecimal("5000000"), "Nguyen Van Hung");
        
        // Xuat tien cho vay
        DebtTransaction lendingTxn = new DebtTransaction(
            System.currentTimeMillis() + 3, new BigDecimal("2000000"), LocalDate.now(), bankAcc, catDebtLending, loanDebt
        );
        service.addTransaction(lendingTxn);
        System.out.println("Da cho vay: 2,000,000");

        // Thu hoi no
        DebtTransaction repaymentTxn = new DebtTransaction(
            System.currentTimeMillis() + 4, new BigDecimal("1000000"), LocalDate.now(), bankAcc, catDebtCollection, loanDebt
        );
        service.addTransaction(repaymentTxn);
        System.out.println("Da thu no: 1,000,000");

        // ==================================================================
        // PHAN 4: GIAO DICH TU DONG
        // ==================================================================
        System.out.println("\n--- [6] Recurring ---");
        // Tao Category ao cho Recurring neu can
        Category catInternet = new Category(99L, "Internet", CategoryType.EXPENSE);
        
        RecurringSchedule internetBill = new RecurringSchedule(
            1L, "Internet Bill", bankAcc, catInternet, new BigDecimal("250000"), CycleType.MONTHLY
        );
        
        NormalTransaction autoTxn = internetBill.generateTxn();
        if (autoTxn != null) {
            autoTxn.setNote("Auto-Payment Internet");
            service.addTransaction(autoTxn);
            System.out.println("Da tu dong thanh toan Internet: 250,000");
        }

        // ==================================================================
        // PHAN 5: TONG KET
        // ==================================================================
        System.out.println("\n====== TRANG THAI CUOI CUNG (DA LUU FILE) ======");
        printBalance(bankAcc);
        printBalance(cashAcc);
        
        System.out.println("Tong so giao dich da luu: " + DataStore.transactions.size());
        System.out.println("Du lieu da duoc luu tai thu muc du an (.dat files)");
    }

    // --- HELPER METHODS ---
    public static void printBalance(Account acc) {
        if (acc != null) {
            System.out.println("[" + acc.getName() + "] So du: " + formatMoney(acc.getBalance()) + " " + (acc.getCurrency() != null ? acc.getCurrency() : "VND"));
        }
    }

    // DA SUA LOI CU PHAP TAI DAY
    public static String formatMoney(BigDecimal amount) {
        return String.format("%,.0f %s", amount, "VND");
    }
}