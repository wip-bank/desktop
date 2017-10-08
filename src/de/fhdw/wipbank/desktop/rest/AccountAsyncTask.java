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

/**
 * Dient dem Aufruf der REST-Schnittstelle /account/
 *
 * @author Daniel Sawenko
 */
public class AccountAsyncTask {

	private String url;
	private OnAccountUpdateListener listener;
	private String accountNumber;
	private PreferenceService preferenceService;
	private final String RESTSTANDARDPORT = "9998";
	private final String URL_TEMPLATE = "http://%s/rest/account/%s/";

	/**
	 * Dieses Interface muss von allen Klassen implementiert werden,
	 * die AccountAsyncTask nutzen wollen.
	 */
	public interface OnAccountUpdateListener {
		/**
         * Wird aufgerufen, wenn der Aufruf des REST-Service erfolgreich war.
         * Der Listener kann nun den Erfolgsfall weiter verarbeiten.
         */
		void onAccountUpdateSuccess();

		/**
         * Wird aufgerufen, wenn der Aufruf des REST-Service nicht erfolgreich war.
         * Der Listener kann nun den Fehlerfall weiter verarbeiten, etwa in dem die
         * Fehlermeldung ausgegeben wird.
         * @param errorMsg anzuzeigende Fehlermeldung
         */
		void onAccountUpdateError(String errorMsg);
	}

	/**
	 * Kontruktor des AccountAsyncTasks. Bekommt die aufrufende Klasse als Objekt übergeben.
	 * Die aufrufende Klasse muss eine Instanz der Klasse OnAccountUpdateListener sein,
	 * damit im späteren Verlauf die Ergebnisse an diesen Listener zurückgegeben werden können.
	 * @param caller Instanz der aufrufenden Klasse
	 */
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

	/**
	 * Ablauf AsyncTask aus Android nachgebaut (vereinfacht).
	 */
	public void execute() {
		Pair<String, String> responsePair = doInBackground();
		onPostExecute(responsePair);

	}

	/**
	 * Hier wird der REST-Service /account/ aufgerufen.
	 * @param params nicht notwendig; jedoch beibehalten, um mit der Android-Variante übereinzustimmen.
	 * @return Paar von Strings; Im ersten Eintrag wird der JSON eines Accounts zurückgegeben (im Erfolgsfall); Im zweiten Eintrag ein ResponseCode (im Fehlerfall)
	 */
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

			// Prüfung, ob der ErrorResponse null ist. Falls ja (z.B. falls
			// keine Verbindung zum Server besteht)
			// soll die Methode direkt verlassen und null zurückgegeben werden
			if (response == null)
				return null;
			int responseCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			// Prüfung, ob der ResponseCode OK ist, damit ein JSON-String
			// erwartet und verarbeitet werden kann
			if (responseCode == HttpStatus.SC_OK) {

				return new Pair<String, String>(responseString, null);
			}
			// Falls der ResponseCode nicht OK ist, wird nur der ResponseCode
			// zurückgegeben
			else {
				return new Pair<String, String>(null, responseString);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Hier wird das responsePair weiter verarbeitet. Der JSON wird in ein Account-Objekt konvertiert (im Erfolgsfall).
	 * Im Fehlerfall wird eine adäquate Fehlermeldung zurückgegeben. Rückgaben geschehen jeweils über den Listener.
	 * @param responsePair Hier wird das von doInBackground() zurückgegebene String-Paar zur weiteren Verarbeitung verwendet
	 */
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

	/** Nimmt eine IP als Parameter und setzt die URL des REST-Service mit Hilfe der URL_TEMPLATE.
	 * Falls kein Port in der IP übergeben wurde, so wird der RESTSTANDARDPORT verwendet (9998).
	 * @param ip IP des REST-Service
	 */
	public void setUrl(String ip) {
		if (!ip.contains(":")) {
			ip = ip + ":" + RESTSTANDARDPORT;
		}
		url = String.format(URL_TEMPLATE, ip, accountNumber);
	}

}
