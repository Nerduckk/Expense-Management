package com.mycompany.appquanlychitieu.service;

import com.mycompany.appquanlychitieu.model.*;
import static com.raven.main.Main.dataStore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public void addTransaction(AbstractTransaction txn) {
        processBalanceUpdate(txn);
        DataStore.transactions.add(txn);
        DataStore.saveData();
        System.out.println("Giao dịch đã được ghi nhận!");
    }

    private void processBalanceUpdate(AbstractTransaction txn) {
        if (txn instanceof TransferTransaction) {
            TransferTransaction tTxn = (TransferTransaction) txn;
            BigDecimal totalDeduct = tTxn.getAmount().add(tTxn.getTransferFee());
            tTxn.getSourceAccount().debit(totalDeduct);
            tTxn.getToAccount().credit(tTxn.getAmount());
        } else {
            Account acc = txn.getSourceAccount();
            if (txn.isExpense()) {
                acc.debit(txn.getAmount());
            } else if (txn.isIncome()) {
                acc.credit(txn.getAmount());
            }
        }
    }
    public void deleteTransaction(AbstractTransaction txn) {
    // Hoàn tác số dư trước
    if (txn instanceof TransferTransaction) {
        // Giao dịch chuyển khoản
        TransferTransaction tTxn = (TransferTransaction) txn;
        BigDecimal totalDeduct = tTxn.getAmount().add(tTxn.getTransferFee());

        // Lúc add: source.debit(totalDeduct), to.credit(amount)
        // => Undo: source.credit(totalDeduct), to.debit(amount)
        tTxn.getSourceAccount().credit(totalDeduct);
        tTxn.getToAccount().debit(tTxn.getAmount());
    } else {
        // Giao dịch Thu/Chi
        Account acc = txn.getSourceAccount();
        if (acc != null) {
            if (txn.isIncome()) {
                // Lúc add: credit => undo: debit
                acc.debit(txn.getAmount());
            } else if (txn.isExpense()) {
                // Lúc add: debit => undo: credit
                acc.credit(txn.getAmount());
            }
        }
    }

    // Rồi mới xóa khỏi datastore
    DataStore.transactions.remove(txn);
    DataStore.saveData();
}
    public BigDecimal getTotalBalance() {
        return DataStore.accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalIncomeMonth(int month, int year) {
        return DataStore.transactions.stream()
                .filter(tx -> tx instanceof NormalTransaction)
                .map(tx -> (NormalTransaction) tx)
                .filter(NormalTransaction::isIncome)
                .filter(tx -> tx.getDate().getMonthValue() == month
                           && tx.getDate().getYear() == year)
                .map(AbstractTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpenseMonth(int month, int year) {
        return DataStore.transactions.stream()
                .filter(tx -> tx instanceof NormalTransaction)
                .map(tx -> (NormalTransaction) tx)
                .filter(NormalTransaction::isExpense)
                .filter(tx -> tx.getDate().getMonthValue() == month
                           && tx.getDate().getYear() == year)
                .map(AbstractTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int countTransactionsMonth(int month, int year) {
        return (int) DataStore.transactions.stream()
                .filter(tx -> tx.getDate().getMonthValue() == month
                           && tx.getDate().getYear() == year)
                .count();
    }

    // ===== HÀM FILTER MỚI – DÙNG isIncome(), isExpense(), getSourceAccount() =====
    public List<AbstractTransaction> filter(
            LocalDate from,
            LocalDate to,
            CategoryType type,
            Long accountId
    ) {
        return DataStore.transactions.stream()
                .filter(t -> (from == null || !t.getDate().isBefore(from)))
                .filter(t -> (to == null || !t.getDate().isAfter(to)))
                .filter(t -> {
                    if (type == null) return true;           // không lọc theo loại
                    if (type == CategoryType.INCOME) {
                        return t.isIncome();
                    }
                    if (type == CategoryType.EXPENSE) {
                        return t.isExpense();
                    }
                    return true;
                })
                .filter(t -> {
                    if (accountId == null) return true;       // không lọc theo ví
                    Account acc = t.getSourceAccount();
                    return acc != null && acc.getId().equals(accountId);
                })
                .toList();
    }
    public List<AbstractTransaction> getAll() {
        return DataStore.transactions;
    }
}
