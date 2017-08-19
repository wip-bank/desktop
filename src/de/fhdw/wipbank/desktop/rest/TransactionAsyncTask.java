package de.fhdw.wipbank.desktop.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fhdw.wipbank.desktop.model.ErrorResponse;
import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import javafx.util.Pair;

public class TransactionAsyncTask {

	private String url;
	private OnTransactionExecuteListener listener;
	private PreferenceService preferenceService;
	private Transaction transaction;
	private final String RESTSTANDARDPORT = "9998";

	/**
	 * This interface must be implemented by classes that use the
	 * TransactionAsyncTask
	 */
	public interface OnTransactionExecuteListener {
		void onTransactionSuccess();

		void onTransactionError(String response);
	}

	public TransactionAsyncTask(Transaction transaction, Object caller) {
		if (caller instanceof OnTransactionExecuteListener) {
			listener = (OnTransactionExecuteListener) caller;
		} else {
			throw new RuntimeException(caller.toString() + " must implement OnTransactionExecuteListener");
		}

		this.transaction = transaction;

		preferenceService = new PreferenceService();

		url = "http://localhost:9998/rest/transaction";
	}
	
	public void execute() {
		HttpResponse response = doInBackground();
		onPostExecute(response);

	}

	protected HttpResponse doInBackground() {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 1500;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("senderNumber", transaction.getSender().getNumber()));
			nameValuePairs.add(new BasicNameValuePair("receiverNumber", transaction.getReceiver().getNumber()));
			nameValuePairs.add(new BasicNameValuePair("amount", String.valueOf(transaction.getAmount())));
			nameValuePairs.add(new BasicNameValuePair("reference", transaction.getReference()));
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
			httppost.setEntity(encodedFormEntity);
			httppost.setHeader("Accept", "application/json"); // Akzeptiert wird ein JSON (ErrorResponse falls
																// vorhanden)

			HttpResponse response = httpClient.execute(httppost);

			// Prüfung, ob der ErrorResponse null ist. Falls ja (z.B. falls keine
			// Verbindung zum Server besteht)
			// soll die Methode direkt verlassen und null zurückgegeben werden
			if (response == null)
				return null;

			return response;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(HttpResponse response) {
		if (listener == null)
			return;

		if (response == null) {
			listener.onTransactionError("Keine Verbindung zum Server");
			return;
		}
		int responseCode = response.getStatusLine().getStatusCode();

		if (responseCode == HttpStatus.SC_OK) {
			listener.onTransactionSuccess();
		} else {
			try {
				Gson gson = new GsonBuilder().create();
				ErrorResponse errorResponse = gson.fromJson(EntityUtils.toString(response.getEntity(), "UTF-8"),
						ErrorResponse.class);
				String errorMsg = errorResponse.getError();
				listener.onTransactionError(errorMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}