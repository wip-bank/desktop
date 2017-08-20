package de.fhdw.wipbank.desktop.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PreferenceService {

	private static final String pref_accountNumber_key = "accountnumber";
	private static final String pref_backup_account_key = "backup_account";
	private static final String pref_server_ip_key = "server_ip";
	
	private String accountNumber;
	private String backupAccount;
	private String serverIP;
	
	public PreferenceService() {
		accountNumber = "";
		backupAccount = "";
		serverIP = "";
	}
	
	/**
	 * http://www.drdobbs.com/jvm/readwrite-properties-files-in-java/231000005
	 */
	public synchronized void saveParamChangesAsXML() {
	    try {
	        Properties props = new Properties();
	        props.setProperty(pref_accountNumber_key, accountNumber);
	        props.setProperty(pref_backup_account_key, backupAccount);
	        props.setProperty(pref_server_ip_key, serverIP);
	        File f = new File("properties.xml");
	        OutputStream out = new FileOutputStream( f );
	        props.storeToXML(out, "WIP-Bank Application configuration properties");
	    }
	    catch (Exception e ) {
	        e.printStackTrace();
	    }
	}
	
	public synchronized void loadProperties() {
	    Properties props = new Properties();
	    InputStream is = null;
	 
	    // First try loading from the current directory
	    try {
	        File f = new File("properties.xml");
	        is = new FileInputStream( f );
	    }
	    catch ( Exception e ) { is = null; }
	 
	    try {
	        if ( is == null ) {
	            // Try loading from classpath
	            is = getClass().getResourceAsStream("properties.xml");
	        }
	 
	        // Try loading properties from the file (if found)
	        props.loadFromXML( is );
	    }
	    catch ( Exception e ) { }
	 
	    accountNumber = props.getProperty(pref_accountNumber_key, "");
	    backupAccount = props.getProperty(pref_backup_account_key, "");
	    serverIP = props.getProperty(pref_server_ip_key, "");
	}

	public String getAccountNumber() {
		loadProperties();
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
		saveParamChangesAsXML();
	}

	public String getBackupAccount() {
		loadProperties();
		return backupAccount;
	}

	public void setBackupAccount(String backupAccount) {
		this.backupAccount = backupAccount;
		saveParamChangesAsXML();
	}

	public String getServerIP() {
		loadProperties();
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
		saveParamChangesAsXML();
	}
	
	
	
}
