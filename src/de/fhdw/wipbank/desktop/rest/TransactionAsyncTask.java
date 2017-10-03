package de.fhdw.wipbank.desktop.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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

import de.fhdw.wipbank.desktop.model.Transaction;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import javafx.util.Pair;

/**
 * Dient dem Aufruf der REST-Schnittstelle /transaction/
 */
public class TransactionAsyncTask {

	private String url;
	private OnTransactionExecuteListener listener;
	private PreferenceService preferenceService;
	private Transaction transaction;
	private final String RESTSTANDARDPORT = "9998";
	private final String URL_TEMPLATE = "http://%s/rest/transaction";

	/**
	 * Dieses Interface muss von allen Klassen implementiert werden,
	 * die TransactionAsyncTask nutzen wollen.
	 */
	public interface OnTransactionExecuteListener {
		void onTransactionSuccess();

		void onTransactionError(String response);
	}

	/**
	 * Kontruktor des TransactionAsyncTask. Bekommt eine durchzuführende Transaktion übergeben.
	 * Bekommt die aufrufende Klasse als Objekt übergeben.
	 * Die aufrufende Klasse muss eine Instanz der Klasse OnTransactionExecuteListener sein,
	 * damit im späteren Verlauf die Ergebnisse an diesen Listener zurückgegeben werden können.
	 * @param transaction
	 * @param caller
	 */
	public TransactionAsyncTask(Transaction transaction, Object caller) {
		if (caller instanceof OnTransactionExecuteListener) {
			listener = (OnTransactionExecuteListener) caller;
		} else {
			throw new RuntimeException(caller.toString() + " must implement OnTransactionExecuteListener");
		}

		this.transaction = transaction;

		preferenceService = new PreferenceService();

		setUrl(preferenceService.getServerIP());
	}

	/**
	 * Ablauf AsyncTask aus Android nachgebaut (vereinfacht).
	 */
	public void execute() {
		Pair<Integer, String> responsePair = doInBackground();
		onPostExecute(responsePair);

	}

	/**
	 * @return
	 */
	protected Pair<Integer, String> doInBackground() {
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
			nameValuePairs.add(new BasicNameValuePair("amount", transaction.getAmount().toPlainString()));
			nameValuePairs.add(new BasicNameValuePair("reference", transaction.getReference()));
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
			httppost.setEntity(encodedFormEntity);

			HttpResponse response = httpClient.execute(httppost);

			//Prüfung, ob der ErrorResponse null ist. Falls ja (z.B. falls keine Verbindung zum Server besteht)
            //soll die Methode direkt verlassen und null zurückgegeben werden
            if (response == null) return null;

            int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            return new Pair<Integer, String>(responseCode, responseString);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(Pair<Integer, String> responsePair) {
		  if (listener == null)
	            return;

	        if (responsePair.getKey() == null || responsePair == null) {
	            listener.onTransactionError("Keine Verbindung zum Server");
	            return;
	        }

	        if (responsePair.getKey() == HttpStatus.SC_OK) {
	            listener.onTransactionSuccess();
	        } else {
	            listener.onTransactionError(responsePair.getValue());
	        }

	}

	public void setUrl(String ip) {
		if (!ip.contains(":")) {
			ip = ip + ":" + RESTSTANDARDPORT;
		}
		url = String.format(URL_TEMPLATE, ip);
	}

}