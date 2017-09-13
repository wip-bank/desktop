package de.fhdw.wipbank.desktop.rest;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fhdw.wipbank.desktop.model.Account;
import de.fhdw.wipbank.desktop.service.AccountService;
import de.fhdw.wipbank.desktop.service.PreferenceService;
import javafx.util.Pair;

public class AccountAsyncTask {

	private String url;
	private OnAccountUpdateListener listener;
	private String accountNumber;
	private PreferenceService preferenceService;
	private final String RESTSTANDARDPORT = "9998";
	private final String URL_TEMPLATE = "http://%s/rest/account/%s/";

	/**
	 * This interface must be implemented by classes that use the
	 * AccountAsyncTask
	 */
	public interface OnAccountUpdateListener {
		void onAccountUpdateSuccess();

		void onAccountUpdateError(String errorMsg);
	}

	public AccountAsyncTask(Object caller) {
		if (caller instanceof AccountAsyncTask.OnAccountUpdateListener) {
			listener = (AccountAsyncTask.OnAccountUpdateListener) caller;
		} else {
			throw new RuntimeException(caller.toString() + " must implement OnAccountUpdateListener");
		}

		// getAccountNumber

		preferenceService = new PreferenceService();
		accountNumber = preferenceService.getAccountNumber();

		setUrl(preferenceService.getServerIP());
	}

	public void execute() {
		Pair<String, String> responsePair = doInBackground();
		onPostExecute(responsePair);

	}

	protected Pair<String, String> doInBackground(Void... params) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 1500;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);

			// Pr�fung, ob der ErrorResponse null ist. Falls ja (z.B. falls
			// keine Verbindung zum Server besteht)
			// soll die Methode direkt verlassen und null zur�ckgegeben werden
			if (response == null)
				return null;
			int responseCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			// Pr�fung, ob der ResponseCode OK ist, damit ein JSON-String
			// erwartet und verarbeitet werden kann
			if (responseCode == HttpStatus.SC_OK) {

				return new Pair<String, String>(responseString, null);
			}
			// Falls der ResponseCode nicht OK ist, wird nur der ResponseCode
			// zur�ckgegeben
			else {
				return new Pair<String, String>(null, responseString);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(Pair<String, String> responsePair) {
		Account account;
		// Falls das Pair nicht null (und damit der ErrorResponse auch nicht
		// null war)
		// sowie
		// der JSON-String im Pair nicht null ist, kann weitergearbeitet werden
		if (responsePair != null && responsePair.getKey() != null) {
			Gson gson = new GsonBuilder().create();
			// JSON in Java-Objekt konvertieren
			account = gson.fromJson(responsePair.getKey(), Account.class);
			AccountService.setAccount(account);

			// Notify everybody that may be interested.
			if (listener != null) {
				listener.onAccountUpdateSuccess();
			}

			// Backup vom Account (JSON) erstellen. Das Backup wird bei Betrieb
			// ohne
			// Verbindung zum Server als Datengrundlage verwendet
			preferenceService.setBackupAccount(responsePair.getKey());
		}
		// Falls kein JSON-String geliefert wird, wird dem Benutzer hier eine
		// Fehlermeldung ausgegeben
		else {
			Gson gson = new GsonBuilder().create();
			// Backup aus SharedPreferences laden (JSON in Java-Objekt
			// konvertieren)
			String backupAccount = preferenceService.getBackupAccount();
			if (!backupAccount.equals("")) {
				account = gson.fromJson(backupAccount, Account.class);
				AccountService.setAccount(account);
			}

			String errorMsg = null;
			if (responsePair != null) {
				errorMsg = responsePair.getValue();
			}

			if (errorMsg == null)
				errorMsg = "Keine Verbindung zum Server";

			// Notify everybody that may be interested.
			if (listener != null) {
				listener.onAccountUpdateError(errorMsg);
			}
		}
	}

	public void setUrl(String ip) {
		if (!ip.contains(":")) {
			ip = ip + ":" + RESTSTANDARDPORT;
		}
		url = String.format(URL_TEMPLATE, ip, accountNumber);
	}

}
