package com.mycompany.appquanlychitieu.service;

import com.mycompany.appquanlychitieu.model.Account;
import java.util.List;

public class AccountService {

    public List<Account> getAll() {
        return DataStore.accounts;
    }

    public void add(Account acc) {
        DataStore.accounts.add(acc);
        DataStore.saveData();
    }

    public void delete(Account acc) {
        DataStore.accounts.remove(acc);
        DataStore.saveData();
    }

    public void saveChanges() {
        DataStore.saveData();
    }
}
