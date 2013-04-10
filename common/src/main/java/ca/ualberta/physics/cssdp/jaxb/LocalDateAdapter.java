package ca.ualberta.physics.cssdp.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	public LocalDate unmarshal(String v) throws Exception {
		return LocalDate.parse(v, DateTimeFormat.forPattern("yyyy-MM-dd"));
	}

	public String marshal(LocalDate v) throws Exception {
		return v.toString("yyyy-MM-dd");
	}

}