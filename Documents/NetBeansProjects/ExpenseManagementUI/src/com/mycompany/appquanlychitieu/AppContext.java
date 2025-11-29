package com.mycompany.appquanlychitieu;

import com.mycompany.appquanlychitieu.model.User;
import com.mycompany.appquanlychitieu.service.DataStore;
import com.mycompany.appquanlychitieu.service.TransactionService;
import com.mycompany.appquanlychitieu.service.CategoryService;
import com.mycompany.appquanlychitieu.service.AccountService;
import com.mycompany.appquanlychitieu.service.DebtService; 
public class AppContext {
    public static final TransactionService transactionService = new TransactionService();
    public static final CategoryService categoryService = new CategoryService();
    public static final AccountService  accountService  = new AccountService();
    public static final DebtService     debtService     = new DebtService();
    private static User currentUser;

    public static void init() {
        DataStore.loadData();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
