package de.fhdw.wipbank.desktop.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import de.fhdw.wipbank.desktop.main.Main;
import de.fhdw.wipbank.desktop.model.Account;
import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.rest.AccountAsyncTask;
import de.fhdw.wipbank.desktop.service.AccountService;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import de.fhdw.wipbank.desktop.util.CustomAlert;
import de.fhdw.wipbank.desktop.util.TransactionSorter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * Quelle: https://www.turais.de/how-to-custom-listview-cell-in-java
 * 
 * @author Daniel
 *
 */
public class TransactionListController implements Initializable, AccountAsyncTask.OnAccountUpdateListener {

	@FXML
	private AnchorPane anchorPaneTransactionList;

	@FXML
	private ListView<Transaction> listView;

	private ObservableList<Transaction> transactionObservableList;

	@FXML
	private Label labelBalance;

	@FXML
	private Label labelSender;

	@FXML
	private Label labelReceiver;

	@FXML
	private Label labelAmount;

	@FXML
	private Label labelRef;

	@FXML
	private Label labelTransactionDate;

	@FXML
	private Button btnNewTransaction;

	@FXML
	private Button btnRefresh;

	public TransactionListController() {

		transactionObservableList = FXCollections.observableArrayList();

		// add the User's Transactions
		transactionObservableList.addAll(AccountService.getAccount().getTransactions());

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listView.setItems(transactionObservableList);
		listView.setCellFactory(transactionListView -> new TransactionRow());

		List<Transaction> transactions;
		Account account = AccountService.getAccount();
		transactions = account.getTransactions();

		if (transactions == null) {
			transactions = new ArrayList<Transaction>();
		}
		BigDecimal balance = new BigDecimal(0);
		for (Transaction transaction : transactions) {
			if (transaction.getSender().getNumber().equals(account.getNumber()))
				// Benutzer überweist Geld an wen anders
				balance = balance.subtract(transaction.getAmount());
			else
				// Benutzer bekommt Geld
				balance = balance.add(transaction.getAmount());
		}
		switch (balance.compareTo(new BigDecimal(0))) {
		case 1:
			labelBalance.setTextFill(Color.web("#2e7d32"));
			break;
		case -1:
			labelBalance.setTextFill(Color.web("#b71c1c"));
			break;
		case 0:
			labelBalance.setTextFill(Color.web("#212121"));
			break;
		}

		NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
		formatter.setMinimumFractionDigits(2);
		labelBalance.setText(formatter.format(balance));

		listView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showTransactionDetails(newValue));

		// Das erste Listenelement auswählen
		listView.getSelectionModel().select(0);

	}

	private void showTransactionDetails(Transaction transaction) {
		if (transaction != null) {

			PreferenceService preferenceService = new PreferenceService();
			String accountNumber = preferenceService.getAccountNumber();

			Date date = transaction.getTransactionDate();

			labelSender.setText(transaction.getSender().getOwner());
			labelReceiver.setText(transaction.getReceiver().getOwner());
			NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
			formatter.setMinimumFractionDigits(2);
			String amount = formatter.format(transaction.getAmount());
			if (transaction.getSender().getNumber().equals(accountNumber)) {
				// Transaktion vom BenutzerAccount an jemand anders

				// Farbe von Amount rot
				// labelAmount.setColor(ContextCompat.getColor(context,
				// R.color.amount_negative));
				// Minus vor Amount
				amount = "-" + amount;
			} else {
				// Transaktion an den Benutzer
				// labelAmount.setTextColor(ContextCompat.getColor(context,
				// R.color.amount_positive));
			}
			labelRef.setText(transaction.getReference());
			labelAmount.setText(amount);

			DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
			labelTransactionDate.setText(dateFormatter.format(transaction.getTransactionDate()));
		} else {
			labelSender.setText("");
			labelReceiver.setText("");
			labelAmount.setText("");
			labelRef.setText("");
			labelTransactionDate.setText("");

		}
	}

	@FXML
	void onBtnNewTransactionClicked(ActionEvent event) {
		try {
			AnchorPane newTransaction = (AnchorPane) FXMLLoader
					.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/NewTransaction.fxml"));
			Main.getRootLayout().setCenter(newTransaction);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	void onBtnRefreshClicked(ActionEvent event) {
		update();
	}	

	@Override
	public void onAccountUpdateSuccess() {
		transactionObservableList.clear();
		transactionObservableList.addAll(AccountService.getAccount().getTransactions());
		// Das erste Listenelement auswählen
		listView.getSelectionModel().select(0);

	}

	@Override
	public void onAccountUpdateError(String errorMsg) {
		new CustomAlert(AlertType.ERROR, errorMsg, ButtonType.OK).showAndWait();

	}
	
	public void update() {
		new AccountAsyncTask(this).execute();
	}

}