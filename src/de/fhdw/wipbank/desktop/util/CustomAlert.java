package de.fhdw.wipbank.desktop.util;

import de.fhdw.wipbank.desktop.main.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Daniel
 *
 *	Eigene AlertBox mit App-Icon und vereinfachter Kontruktor im Vergleich zu javafx/Alert
 *
 */
public class CustomAlert extends Alert{

	/**
	 * Kontruktor einer eigenen AlertBox. 
	 * Beispielaufruf: new CustomAlert(AlertType.ERROR, "Fehler", ButtonType.OK).showAndWait();
	 * 
	 * 
	 * @param alertType Art des Alerts z.B. Error
	 * @param contentText Anzuzeigender Text
	 * @param buttons Anzuzeigende Buttons (1-n Buttons) z.B. OK, Cancel etc.
	 */
	public CustomAlert(AlertType alertType, String contentText, ButtonType... buttons) {
		super(alertType, contentText, buttons);
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/icon.png")));
	}
	
}
