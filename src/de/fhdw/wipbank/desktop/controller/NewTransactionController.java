package de.fhdw.wipbank.desktop.controller;

import java.math.BigDecimal;

import de.fhdw.wipbank.desktop.main.Main;
import de.fhdw.wipbank.desktop.model.Account;
import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.rest.TransactionAsyncTask;
import de.fhdw.wipbank.desktop.service.AccountService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NewTransactionController implements TransactionAsyncTask.OnTransactionExecuteListener {

	@FXML
	private TextField edtReceiverNumber;

	@FXML
	private TextField edtAmount;

	@FXML
	private TextField edtReference;

	@FXML
	private Button btnExecute;

	@FXML
	private Button btnCancel;

	@FXML
	void onBtnExecuteClicked(ActionEvent event) {
		Transaction transaction = new Transaction();
		Account sender = AccountService.getAccount();
		Account receiver = new Account();
		transaction.setSender(sender);
		receiver.setNumber(edtReceiverNumber.getText());
		transaction.setReceiver(receiver);

		BigDecimal amount;
		try {
			amount = BigDecimal.valueOf(Double.valueOf(edtAmount.getText().toString()));
		} catch (NumberFormatException e) {
			amount = BigDecimal.ZERO;
		}
		transaction.setAmount(amount);
		transaction.setReference(edtReference.getText());
		new TransactionAsyncTask(transaction, this).execute();

	}
	
	private void backToTransactionList() {
		try {
			AnchorPane transactionList = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/TransactionList.fxml"));
			Main.getRootLayout().setCenter(transactionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	void onBtnCancelClicked(ActionEvent event) {
		backToTransactionList();
	}

	@Override
	public void onTransactionSuccess() {
		// TODO Auto-generated method stub
		Alert alert = new Alert(AlertType.INFORMATION, "Transaktion erfolgreich.", ButtonType.OK);
		
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/icon.png")));
		
		alert.show();
		backToTransactionList();
		

	}

	@Override
	public void onTransactionError(String response) {
		// TODO Auto-generated method stub

	}

}
