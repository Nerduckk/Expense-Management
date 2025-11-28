/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.appquanlychitieu;
import com.mycompany.appquanlychitieu.model.*;
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
        System.out.println("====== BAT DAU MO PHONG HE THONG QUAN LY CHI TIEU ======\n");

        System.out.println("--- [1] Khoi tao du lieu ---");
        
        User user = new User(1L, "Duck Developer", "duck@code.com", "hashed_pass", "VND");
        System.out.println("User: " + user.getSummary());


        Account bankAcc = new Account(1L, "MB Bank", new BigDecimal("10000000"), "VND");
        Account cashAcc = new Account(2L, "Vi Tien Mat", new BigDecimal("500000"), "VND");
        
        printBalance(bankAcc);
        printBalance(cashAcc);

        Category catSalary = new Category("Luong", CategoryType.INCOME, "icon_money", "green", null);
        Category catFood = new Category("An uong", CategoryType.EXPENSE, "icon_food", "red", new BigDecimal("2000000"));
        Category catDebtLending = new Category("Cho vay", CategoryType.EXPENSE, "icon_lending", "grey", null);
        Category catDebtCollection = new Category("Thu hoi no", CategoryType.INCOME, "icon_collect", "green", null);


        System.out.println("\n--- [2] Giao dich: Nhan luong ---");
        NormalTransaction salaryTxn = new NormalTransaction(
            1001L, new BigDecimal("15000000"), LocalDate.now(), bankAcc, catSalary
        );
        salaryTxn.setName("Luong Thang 10");

        if (salaryTxn.isIncome()) {
            bankAcc.credit(salaryTxn.getAmount());
            System.out.println("Da nhan: " + formatMoney(salaryTxn.getAmount()) + " vao " + bankAcc.getName());
        }

        System.out.println("\n--- [3] Giao dich: Di an nha hang ---");
        NormalTransaction foodTxn = new NormalTransaction(
            1002L, new BigDecimal("500000"), LocalDate.now(), cashAcc, catFood
        );
        foodTxn.setName("An toi");

        if (foodTxn.isExpense()) {
            cashAcc.debit(foodTxn.getAmount());
            System.out.println("Da chi: " + formatMoney(foodTxn.getAmount()) + " tu " + cashAcc.getName());
            
            if (catFood.isOverBudget(foodTxn.getAmount())) {
                System.out.println("CANH BAO: Vuot qua ngan sach!");
            } else {
                System.out.println("Ngan sach van on dinh.");
            }
        }

        System.out.println("\n--- [4] Giao dich: Rut tien ve Vi ---");
        TransferTransaction transferTxn = new TransferTransaction(
            1003L, new BigDecimal("2000000"), LocalDate.now(), bankAcc, cashAcc
        );
        transferTxn.setName("Rut tien tieu vat");
        transferTxn.setTransferFee(new BigDecimal("1100"));

        bankAcc.debit(transferTxn.getAmount());
        bankAcc.debit(new BigDecimal("1100")); 
        cashAcc.credit(transferTxn.getAmount());
        
        System.out.println("Chuyen khoan thanh cong.");
        printBalance(bankAcc);
        printBalance(cashAcc);

        System.out.println("\n--- [5] Quan ly No: Chu trinh Cho vay & Thu no ---");
        
        Debt loanDebt = new Debt();
        loanDebt.setName("Khoan no cua Hung");
        loanDebt.setPersonName("Nguyen Van Hung");
        loanDebt.setPrincipalAmount(new BigDecimal("5000000")); 
        loanDebt.setType(DebtType.LENDING);
        loanDebt.setTransactions(new ArrayList<>());

        DebtTransaction lendingTxn = new DebtTransaction(
            new BigDecimal("2000000"), LocalDate.now(), bankAcc, catDebtLending, loanDebt
        );
        lendingTxn.setName("Giai ngan dot 1");
        
        bankAcc.debit(lendingTxn.getAmount());
        loanDebt.getTransactions().add(lendingTxn);
        System.out.println("Da cho Hung vay: " + formatMoney(lendingTxn.getAmount()));

        DebtTransaction repaymentTxn = new DebtTransaction(
            new BigDecimal("1000000"), LocalDate.now(), bankAcc, catDebtCollection, loanDebt
        );
        repaymentTxn.setName("Hung tra bot tien");
        
        bankAcc.credit(repaymentTxn.getAmount());
        loanDebt.getTransactions().add(repaymentTxn); 
        System.out.println("Hung da tra: " + formatMoney(repaymentTxn.getAmount()));

        System.out.println(">> Tinh trang no hien tai:");
        System.out.println("   - Tong goc: " + formatMoney(loanDebt.getPrincipalAmount()));
        System.out.println("   - Da dua:   " + formatMoney(lendingTxn.getAmount())); 
        System.out.println("   - Con lai phai thu (tuong doi): " + formatMoney(loanDebt.getRemainingAmount()));

        System.out.println("\n--- [6] Giao dich tu dong (Recurring) ---");
        RecurringSchedule internetBill = new RecurringSchedule(
            bankAcc, 
            new Category("Internet", CategoryType.EXPENSE, "wifi", "blue", null), 
            new BigDecimal("250000"), 
            CycleType.MONTHLY, LocalDate.now(), 0, LocalTime.of(8, 0), true, LocalDate.now().plusYears(1), 12
        );
        
        NormalTransaction autoTxn = internetBill.generateTxn();
        if (autoTxn != null) {
            autoTxn.setName("Auto-Payment Internet");
            bankAcc.debit(autoTxn.getAmount()); 
            System.out.println("He thong tu dong thanh toan: " + autoTxn.getName() + " | So tien: " + formatMoney(autoTxn.getAmount()));
        }

        System.out.println("\n--- [7] Test logic: Tieu qua so du ---");
        BigDecimal itemPrice = new BigDecimal("50000000"); 
        System.out.println("Dang co gang mua xe may gia: " + formatMoney(itemPrice));
        
        if (cashAcc.getBalance().compareTo(itemPrice) < 0) {
            System.out.println("GIAO DICH THAT BAI: So du khong du!");
        } else {
            cashAcc.debit(itemPrice);
        }


        System.out.println("\n====== BAO CAO TONG KET ======");
        
        BigDecimal totalIncome = salaryTxn.getAmount().add(repaymentTxn.getAmount());
        BigDecimal totalExpense = foodTxn.getAmount()
                                 .add(lendingTxn.getAmount())
                                 .add(autoTxn != null ? autoTxn.getAmount() : BigDecimal.ZERO)
                                 .add(new BigDecimal("1100"));
                                 
        System.out.println("Tong Thu: " + formatMoney(totalIncome));
        System.out.println("Tong Chi: " + formatMoney(totalExpense));
        System.out.println("------------------------------");
        printBalance(bankAcc);
        printBalance(cashAcc);
        
        System.out.println("\nCHUONG TRINH KET THUC THANH CONG!");
    }

    public static void printBalance(Account acc) {
        System.out.println("[" + acc.getName() + "] So du: " + formatMoney(acc.getBalance()) + " " + acc.getCurrency());
    }

    public static String formatMoney(BigDecimal amount) {
        return String.format("%,.0f", amount);
    }
}
