package de.fhdw.wipbank.desktop.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * DateTimeAdapter zur korrekten Anzeige von Zeitstempeln
 * Quelle: http://javarevisited.blogspot.de/2017/04/jaxb-date-format-example-using-annotation-XMLAdapter.html
 *
 */
public class DateTimeAdapter extends XmlAdapter<String, Date>{
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    public Date unmarshal(String xml) throws Exception {
        return dateFormat.parse(xml);
    }

    @Override
    public String marshal(Date object) throws Exception {
        return dateFormat.format(object);
    }

}
