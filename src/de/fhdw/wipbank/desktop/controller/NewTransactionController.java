package de.fhdw.wipbank.desktop.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import de.fhdw.wipbank.desktop.main.Main;
import de.fhdw.wipbank.desktop.model.Account;
import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.rest.TransactionAsyncTask;
import de.fhdw.wipbank.desktop.service.AccountService;
import de.fhdw.wipbank.desktop.util.CustomAlert;
import de.fhdw.wipbank.desktop.util.CustomTextFormatter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

public class NewTransactionController implements Initializable, TransactionAsyncTask.OnTransactionExecuteListener {

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		edtAmount.setTextFormatter(CustomTextFormatter.getFormatterAmount());
		edtReceiverNumber.setTextFormatter(CustomTextFormatter.getFormatterAccountNumber());
		
		btnExecute.setDefaultButton(true);
		
//		EventHandler fireOnEnter = event -> {
//			   if (KeyCode.ENTER.equals(event.getCode()) && event.getTarget() instanceof Button) {
//			      ((Button) event.getTarget()).fire();
//			   }
//			};
		
	}
	
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
			amount = new BigDecimal(edtAmount.getText().replace(",", "."));
		} catch (NumberFormatException e) {
			amount = BigDecimal.ZERO;
		}
		transaction.setAmount(amount);
		transaction.setReference(edtReference.getText());
		new TransactionAsyncTask(transaction, this).execute();

	}

	@FXML
	void onBtnCancelClicked(ActionEvent event) {
		backToTransactionList();
	}

	private void backToTransactionList() {
		try {
			//AnchorPane transactionList = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/TransactionList.fxml"));
			AnchorPane transactionList = Main.getTransactionList();
			Main.getTransactionListController().update();
			Main.getRootLayout().setCenter(transactionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTransactionSuccess() {
		new CustomAlert(AlertType.INFORMATION, "Transaktion erfolgreich", ButtonType.OK).showAndWait();
		backToTransactionList();
	}

	@Override
	public void onTransactionError(String response) {
		new CustomAlert(AlertType.ERROR, response, ButtonType.OK).show();

	}

	

}
