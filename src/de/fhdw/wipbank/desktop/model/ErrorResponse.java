package de.fhdw.wipbank.desktop.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse {

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
