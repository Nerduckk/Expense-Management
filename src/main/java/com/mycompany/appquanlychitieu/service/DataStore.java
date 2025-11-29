package com.mycompany.appquanlychitieu.service;
import com.mycompany.appquanlychitieu.model.*;
import java.util.List;
/**
 *
 * @author Admin
 */
public class DataStore {
   
    private static final String FILE_ACCOUNTS = "accounts.dat";
    private static final String FILE_CATEGORIES = "categories.dat";
    private static final String FILE_TRANSACTIONS = "transactions.da

    public static List<Account> accounts;
    public static List<Category> categories;
    public static List<AbstractTransaction> transactions;

    public static void loadData() {
        accounts = FileHelper.loadFromFile(FILE_ACCOUNTS);
        categories = FileHelper.loadFromFile(FILE_CATEGORIES);
        transactions = FileHelper.loadFromFile(FILE_TRANSACTIONS);
        System.out.println("--- Đã tải xong dữ liệu ---");
    }

    public static void saveData() {
        FileHelper.saveToFile(accounts, FILE_ACCOUNTS);
        FileHelper.saveToFile(categories, FILE_CATEGORIES);
        FileHelper.saveToFile(transactions, FILE_TRANSACTIONS);
    }
}
