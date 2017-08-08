package de.fhdw.wipbank.desktop.main;

import java.io.IOException;

import de.fhdw.wipbank.desktop.account.AccountAsyncTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//Startklasse, über die die JavaFX Anwendung gestartet und konfiguriert wird
//Vererbung der weiter unten eingesetzten Methoden von der Application-Klasse
public class Main extends Application implements AccountAsyncTask.OnAccountUpdateListener {

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		
		new AccountAsyncTask(this).execute();
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WIP-Bank");
		this.primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/icon.png")));

		initRootLayout();

		
		showTransactionList();
	}

	public static void main(String[] args) {
		// Die launch-Methode führt unter anderem die oben angegeben start-Methode aus
		launch(args);
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
			// Set person overview into the center of root layout.
	        rootLayout.setCenter(transactionList);
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
    public Stage getPrimaryStage() {
        return primaryStage;
    }

}
