package de.fhdw.wipbank.desktop.account;


import de.fhdw.wipbank.desktop.model.Account;

public class AccountService {

    private static Account account;

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account pAccount) {
        account = pAccount;
    }

}
