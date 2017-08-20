package de.fhdw.wipbank.desktop.controller;

import de.fhdw.wipbank.desktop.main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

public class RootLayoutController {

    @FXML
    private MenuItem menuSettings;

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
