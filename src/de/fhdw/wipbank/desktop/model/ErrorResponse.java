package de.fhdw.wipbank.desktop.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse {

    private String error;

    /**
     * Gibt die Fehlermeldung zurück.
     *
     * @return error
     */
    public String getError() {
        return error;
    }

    /**
     * Setzt die Fehlermeldung.
     *
     * @param error
     */
    public void setError(String error) {
        this.error = error;
    }
}
