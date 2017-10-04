package de.fhdw.wipbank.desktop.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * TransactionRow
 * Quelle: https://www.turais.de/how-to-custom-listview-cell-in-javafx/
 */
public class TransactionRow extends ListCell<Transaction> {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label labelDay;

    @FXML
    private Label labelFromTo;

    @FXML
    private Label labelAmount;

    @FXML
    private Label labelMonth;

    @FXML
    private Label labelRef;

    private FXMLLoader mLLoader;



    /* (non-Javadoc)
     * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
     */
    @Override
    protected void updateItem(Transaction transaction, boolean empty) {
        super.updateItem(transaction, empty);



        if(empty || transaction == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/de/fhdw/wipbank/desktop/fxml/TransactionRow.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



        	PreferenceService preferenceService = new PreferenceService();
        	String accountNumber = preferenceService.getAccountNumber();

            Date date = transaction.getTransactionDate();


            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            DecimalFormat dayFormatter = new DecimalFormat("00");

            String day = dayFormatter.format(cal.get(Calendar.DAY_OF_MONTH));
            String month = new SimpleDateFormat("MMM").format(cal.getTime()).toUpperCase();
            labelDay.setText(day);
            labelMonth.setText(month);
            NumberFormat formatter = NumberFormat.getInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            String amount = formatter.format(transaction.getAmount());
            if(transaction.getSender().getNumber().equals(accountNumber)){
                // Transaktion vom BenutzerAccount an jemand anders
                labelFromTo.setText(transaction.getReceiver().getOwner());
                // Farbe von Amount rot
                labelAmount.setTextFill(Color.web("#b71c1c"));
                // Minus vor Amount
                amount = "-" + amount;
            }else{
                // Transaktion an den Benutzer
                labelFromTo.setText(transaction.getSender().getOwner());
                labelAmount.setTextFill(Color.web("#2e7d32"));
            }
            labelRef.setText(transaction.getReference());
            labelAmount.setText(amount);
            labelAmount.setTooltip(new Tooltip(amount));
            setText(null);
            setGraphic(gridPane);
        }

    }
}
