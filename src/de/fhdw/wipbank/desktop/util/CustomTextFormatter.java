package de.fhdw.wipbank.desktop.util;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;

/**
 * Eigener TextFormatter f�r die JavaFX-Textfelder
 * @author Daniel Sawenko
 */
public class CustomTextFormatter {

	private final static Pattern patternAmount = Pattern.compile("^\\d*|\\d+\\.\\d*$");
	private final static Pattern patternAccountNumber = Pattern.compile("^\\d{0,4}$");

	/** Gibt einen TextFormatter f�r Amount-Felder zur�ck.
	 * @return TextFormatter<String>
	 */
	public static TextFormatter<String> getFormatterAmount() {
		return new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) change -> {
		    return patternAmount.matcher(change.getControlNewText()).matches() ? change : null;
		});
	}

	/** Gibt einen TextFormatter f�r AccountNumber-Felder zur�ck.
	 * @return TextFormatter<String>
	 */
	public static TextFormatter<String> getFormatterAccountNumber() {
		return new TextFormatter<String>((UnaryOperator<TextFormatter.Change>) change -> {
		    return patternAccountNumber.matcher(change.getControlNewText()).matches() ? change : null;
		});
	}


}
