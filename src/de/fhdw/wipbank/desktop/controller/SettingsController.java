package de.fhdw.wipbank.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import de.fhdw.wipbank.desktop.main.Main;
import de.fhdw.wipbank.desktop.rest.AccountAsyncTask;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import de.fhdw.wipbank.desktop.util.CustomAlert;
import de.fhdw.wipbank.desktop.util.CustomTextFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


/**
 * Controller f�r das "Settings-Fenster".
 *
 * @author Daniel Sawenko
 */
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

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Speichern wer der Caller ist
		caller = Main.getRootLayout().getCenter();
		preferenceService = new PreferenceService();

		edtAccountNumber.setTextFormatter(CustomTextFormatter.getFormatterAccountNumber());

		edtAccountNumber.setText(preferenceService.getAccountNumber());
		edtServerIP.setText(preferenceService.getServerIP());

		btnSave.setDefaultButton(true);
	}

	/** Verarbeitung des Cancel-Buttons
	 * @param event Eingehendes Event
	 */
	@FXML
	void onBtnCancelClicked(ActionEvent event) {
		backToCaller();
	}

	/** Verarbeitung des Save-Buttons
	 * @param event Eingehendes Event
	 */
	@FXML
	void onBtnSaveClicked(ActionEvent event) {
		preferenceService.setAccountNumber(edtAccountNumber.getText());
		preferenceService.setServerIP(edtServerIP.getText());
		backToCaller();
	}

	/**
	 * Diese Methode ruft das vorherige Fenster auf und dient als R�ckschritt im Aufrufstack.
	 * Wird z.B. in onBtnCancelClicked() aufgerufen.
	 */
	private void backToCaller() {
		try {
			if (caller == null) {
				new AccountAsyncTask(this).execute(); // FirstStart -> Initiales Laden des Accounts, dann Transaktionsliste �ffnen (siehe unten)
			} else {
				Main.getTransactionListController().update(); // Transaktionliste updaten
				Main.getRootLayout().setCenter(caller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.fhdw.wipbank.desktop.rest.AccountAsyncTask.OnAccountUpdateListener#onAccountUpdateSuccess()
	 */
	@Override
	public void onAccountUpdateSuccess() {
		try {

			AnchorPane transactionList = Main.getTransactionList();

			if (transactionList == null) {
				// Wird ausgef�hrt falls transactionList = null -> Beim allerersten Start :)
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/TransactionList.fxml"));
				transactionList = fxmlLoader.load();
				Main.setTransactionList(transactionList);
				Main.setTransactionListController(fxmlLoader.getController());
			}

			Main.getRootLayout().setCenter(transactionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.fhdw.wipbank.desktop.rest.AccountAsyncTask.OnAccountUpdateListener#onAccountUpdateError(java.lang.String)
	 */
	@Override
	public void onAccountUpdateError(String errorMsg) {
		new CustomAlert(AlertType.ERROR, errorMsg, ButtonType.OK).showAndWait();
	}

}
