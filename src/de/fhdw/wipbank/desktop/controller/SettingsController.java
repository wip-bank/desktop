package de.fhdw.wipbank.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import de.fhdw.wipbank.desktop.main.Main;
import de.fhdw.wipbank.desktop.rest.AccountAsyncTask;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class SettingsController implements Initializable, AccountAsyncTask.OnAccountUpdateListener {

	@FXML
	private TextField edtAccountNumber;

	@FXML
	private TextField edtServerIP;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	private Node caller;
	
	private PreferenceService preferenceService;

	@FXML
	void onBtnCancelClicked(ActionEvent event) {
		backToCaller();
	}

	@FXML
	void onBtnSaveClicked(ActionEvent event) {
		preferenceService.setAccountNumber(edtAccountNumber.getText());
		preferenceService.setServerIP(edtServerIP.getText());
		backToCaller();
	}

	private void backToCaller() {
		try {
			if (caller == null) {
				new AccountAsyncTask(this).execute();
			} else {
				Main.getRootLayout().setCenter(caller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Speichern wer der Caller ist
		caller = Main.getRootLayout().getCenter();
		preferenceService = new PreferenceService();
		
		edtAccountNumber.setText(preferenceService.getAccountNumber());
		edtServerIP.setText(preferenceService.getServerIP());
	}

	@Override
	public void onAccountUpdateSuccess() {
		try {
			AnchorPane transactionList = (AnchorPane) FXMLLoader
					.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/TransactionList.fxml"));
			Main.getRootLayout().setCenter(transactionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAccountUpdateError(String errorMsg) {

	}

}
