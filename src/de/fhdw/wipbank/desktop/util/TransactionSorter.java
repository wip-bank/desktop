package de.fhdw.wipbank.desktop.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhdw.wipbank.desktop.model.Account;
import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.service.AccountService;

public class TransactionSorter {
	public static void sortTransactionsFromAccount() {
		System.out.println("try to sort");
		Account account = AccountService.getAccount();
		List<Transaction> transactions = account.getTransactions();
		Collections.sort(transactions, new Comparator<Transaction>() {

			public int compare(Transaction t1, Transaction t2) {
				if (t1.getTransactionDate() == null || t2.getTransactionDate() == null)
					return 0;
				return t2.getTransactionDate().compareTo(t1.getTransactionDate());
			}
		});
		account.setTransactions(transactions);

	}
}
