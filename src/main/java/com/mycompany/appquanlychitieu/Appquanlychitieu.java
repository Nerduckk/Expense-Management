/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.appquanlychitieu;
import com.mycompany.appquanlychitieu.model.*;
import com.mycompany.appquanlychitieu.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
/**
 *
 * @author Duck
 */
public class Appquanlychitieu {

    public static void main(String[] args) {
        System.out.println("====== BAT DAU HE THONG QUAN LY CHI TIEU (CO LUU FILE) ======\n");

        DataStore.loadData();
        TransactionService service = new TransactionService();

        System.out.println("--- [1] Khoi tao du lieu ---");

        Account bankAcc = null; 
        Account cashAcc = null;
        Category catSalary, catFood, catDebtLending, catDebtCollection;

        if (DataStore.accounts.isEmpty()) {
            System.out.println(">> Phat hien lan chay dau tien. Tao du lieu mau...");
            
            User user = new User(1L, "Duck Developer", "duck@code.com", "VND"); 
            
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

            DataStore.saveData();
        } else {
            System.out.println(">> Da tim thay du lieu cu tu File.");
            if (DataStore.accounts.size() >= 1) bankAcc = DataStore.accounts.get(0);
            if (DataStore.accounts.size() >= 2) cashAcc = DataStore.accounts.get(1);
            
            catSalary = DataStore.categories.isEmpty() ? new Category(0L, "Ao", CategoryType.INCOME) : DataStore.categories.get(0);
            catFood = DataStore.categories.size() > 1 ? DataStore.categories.get(1) : catSalary;
            catDebtLending = DataStore.categories.size() > 2 ? DataStore.categories.get(2) : catSalary;
            catDebtCollection = DataStore.categories.size() > 3 ? DataStore.categories.get(3) : catSalary;
        }

        if (bankAcc == null || cashAcc == null) {
            System.err.println("LOI: Du lieu file khong du tai khoan de chay demo. Hay xoa file .dat va chay lai.");
            return;
        }

        printBalance(bankAcc);
        printBalance(cashAcc);

        System.out.println("\n--- [2] Giao dich: Nhan luong ---");
        NormalTransaction salaryTxn = new NormalTransaction(
            System.currentTimeMillis(), new BigDecimal("15000000"), LocalDate.now(), bankAcc, catSalary
        );
        salaryTxn.setNote("Luong Thang 10");
        service.addTransaction(salaryTxn); 

        System.out.println("\n--- [3] Giao dich: Di an nha hang ---");
        NormalTransaction foodTxn = new NormalTransaction(
            System.currentTimeMillis() + 1, new BigDecimal("500000"), LocalDate.now(), cashAcc, catFood
        );
        foodTxn.setNote("An toi");
        service.addTransaction(foodTxn); 

        System.out.println("\n--- [4] Giao dich: Rut tien ve Vi ---");
        TransferTransaction transferTxn = new TransferTransaction(
            System.currentTimeMillis() + 2, new BigDecimal("2000000"), LocalDate.now(), bankAcc, cashAcc, new BigDecimal("1100")
        );
        transferTxn.setNote("Rut tien tieu vat");
        service.addTransaction(transferTxn);

        printBalance(bankAcc);
        printBalance(cashAcc);

        System.out.println("\n--- [5] Quan ly No ---");
        
        Debt loanDebt = new Debt(1L, "No cua Hung", DebtType.LENDING, new BigDecimal("5000000"), "Nguyen Van Hung");
        
        DebtTransaction lendingTxn = new DebtTransaction(
            System.currentTimeMillis() + 3, new BigDecimal("2000000"), LocalDate.now(), bankAcc, catDebtLending, loanDebt
        );
        service.addTransaction(lendingTxn);
        System.out.println("Da cho vay: 2,000,000");

        DebtTransaction repaymentTxn = new DebtTransaction(
            System.currentTimeMillis() + 4, new BigDecimal("1000000"), LocalDate.now(), bankAcc, catDebtCollection, loanDebt
        );
        service.addTransaction(repaymentTxn);
        System.out.println("Da thu no: 1,000,000");

        System.out.println("\n--- [6] Recurring ---");
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
        
        System.out.println("\n====== TRANG THAI CUOI CUNG (DA LUU FILE) ======");
        printBalance(bankAcc);
        printBalance(cashAcc);
        
        System.out.println("Tong so giao dich da luu: " + DataStore.transactions.size());
        System.out.println("Du lieu da duoc luu tai thu muc du an (.dat files)");
    }

    public static void printBalance(Account acc) {
        if (acc != null) {
            System.out.println("[" + acc.getName() + "] So du: " + formatMoney(acc.getBalance()) + " " + (acc.getCurrency() != null ? acc.getCurrency() : "VND"));
        }
    }

    public static String formatMoney(BigDecimal amount) {
        return String.format("%,.0f %s", amount, "VND");
    }
}
