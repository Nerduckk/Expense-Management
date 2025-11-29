package com.mycompany.appquanlychitieu.service;
import com.mycompany.appquanlychitieu.model.*;
import java.math.BigDecimal;
/**
 *
 * @author Duck
 */
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
        } 
        else {
            Account acc = txn.getSourceAccount();
            if (txn.isExpense()) {
                acc.debit(txn.getAmount());
            } else if (txn.isIncome()) {
                acc.credit(txn.getAmount());
            }
        }
    }
    public void deleteTransaction(AbstractTransaction txn) {
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
            .filter(tx -> tx.isIncome())
            .filter(tx -> tx.getDate().getMonthValue() == month && tx.getDate().getYear() == year)
            .map(AbstractTransaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpenseMonth(int month, int year) {
        return DataStore.transactions.stream()
            .filter(tx -> tx instanceof NormalTransaction)
            .map(tx -> (NormalTransaction) tx)
            .filter(tx -> tx.isExpense())
            .filter(tx -> tx.getDate().getMonthValue() == month && tx.getDate().getYear() == year)
            .map(AbstractTransaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int countTransactionsMonth(int month, int year) {
        return (int) DataStore.transactions.stream()
            .filter(tx -> tx.getDate().getMonthValue() == month && tx.getDate().getYear() == year)
            .count();
    }
}
