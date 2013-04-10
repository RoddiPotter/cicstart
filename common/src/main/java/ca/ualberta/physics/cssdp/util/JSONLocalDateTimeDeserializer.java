package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JSONLocalDateTimeDeserializer extends
		StdDeserializer<LocalDateTime> {

	private static final long serialVersionUID = 1L;

	public JSONLocalDateTimeDeserializer() {
		super(LocalDateTime.class);
	}

	@Override
	public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			return ISODateTimeFormat.dateTimeParser().parseLocalDateTime(
					jp.getText());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JsonParseException(e.getMessage(), jp.getCurrentLocation());
		}
	}

}
