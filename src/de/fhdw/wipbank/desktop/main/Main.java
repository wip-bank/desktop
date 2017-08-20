package de.fhdw.wipbank.desktop.main;

import java.io.IOException;

import de.fhdw.wipbank.desktop.rest.AccountAsyncTask;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//Startklasse, über die die JavaFX Anwendung gestartet und konfiguriert wird
//Vererbung der weiter unten eingesetzten Methoden von der Application-Klasse
public class Main extends Application implements AccountAsyncTask.OnAccountUpdateListener {

	private static Stage primaryStage;
	private static BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		
		
		Main.primaryStage = primaryStage;
		Main.primaryStage.setTitle("WIP-Bank");
		Main.primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/icon.png")));

		initRootLayout();

		PreferenceService preferenceService = new PreferenceService();
		if (preferenceService.getAccountNumber().equals("") || preferenceService.getServerIP().equals("")) {
			// Beim ersten Start muss der Benutzer Account-Number und Server-IP eingeben
			showSettings();
		}else {
			new AccountAsyncTask(this).execute();
			showTransactionList();
		}
	}
	
	public static void main(String[] args) {
		// Die launch-Methode führt unter anderem die oben angegeben start-Methode aus
		Main.launch(args);
	}
	
	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/de/fhdw/wipbank/desktop/fxml/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets()
			.add(getClass().getResource("/de/fhdw/wipbank/desktop/styles/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showTransactionList() {
		try {
			AnchorPane transactionList = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/TransactionList.fxml"));
			rootLayout.setCenter(transactionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showSettings() {
		try {
			AnchorPane settings = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/Settings.fxml"));
			rootLayout.setCenter(settings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onAccountUpdateSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccountUpdateError(String errorMsg) {
		// TODO Auto-generated method stub

	}
	
	 /**
     * Returns the main stage.
     * @return
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

	public static BorderPane getRootLayout() {
		return rootLayout;
	}
    
    

}
