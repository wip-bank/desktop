package de.fhdw.wipbank.desktop.service;


import java.util.Collections;
import java.util.Comparator;

import de.fhdw.wipbank.desktop.model.Account;
import de.fhdw.wipbank.desktop.model.Transaction;

public class AccountService {

    private static Account account;

    /**
     * Gibt den Account des Benutzers zur�ck.
     * 
     * @return Account des Benutzers
     */
    public static Account getAccount() {
        return account;
    }

    /**
     * Setzt den Account des Benutzers auf den �bergebenen Account.
     *
     * Es werden zus�tzlich alle Transaktionen nach TransactionDate sortiert: die neusten Transaktionen sind dann zuerst in der Liste.
     * Quelle: https://stackoverflow.com/a/5927408
     *
     * @param pAccount zu setzender Account
     */
    public static void setAccount(Account pAccount) {

        // Transaktionen sortieren: neuste Transaktionen zuerst
        Collections.sort(pAccount.getTransactions(), new Comparator<Transaction>() {

            public int compare(Transaction t1, Transaction t2) {
                if (t2.getTransactionDate() == null || t1.getTransactionDate() == null)
                    return 0;
                return t2.getTransactionDate().compareTo(t1.getTransactionDate());
            }
        });

        // Account setzen
        account = pAccount;
    }

}
