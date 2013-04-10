package ca.ualberta.physics.cssdp.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

	public LocalTime unmarshal(String v) throws Exception {
		return LocalTime.parse(v, DateTimeFormat.forPattern("HH:mm"));
	}

	public String marshal(LocalTime v) throws Exception {
		return v.toString("HH:mm");
	}

}