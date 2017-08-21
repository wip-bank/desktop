package de.fhdw.wipbank.desktop.controller;

import de.fhdw.wipbank.desktop.main.Main;
import de.fhdw.wipbank.desktop.util.CustomAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public class RootLayoutController {

	@FXML
	private MenuItem menuAboutUs;

	@FXML
	private MenuItem menuSettings;

	@FXML
	void onMenuAboutUsClicked(ActionEvent event) {
		CustomAlert customAlert = new CustomAlert(AlertType.INFORMATION, "By Alexander, Daniel, Jannis & Philipp", ButtonType.OK);
		customAlert.setTitle("Über uns");
		customAlert.setHeaderText("WIP-Bank");
		customAlert.showAndWait();
	}

	@FXML
	void onMenuSettingsClicked(ActionEvent event) {
		try {
			AnchorPane settings = (AnchorPane) FXMLLoader
					.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/Settings.fxml"));
			Main.getRootLayout().setCenter(settings);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
