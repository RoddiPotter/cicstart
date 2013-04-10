package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JSONClassDeserializer extends StdDeserializer<Class<?>> {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(JSONClassDeserializer.class);

	public JSONClassDeserializer() {
		super(Class.class);
	}

	@Override
	public Class<?> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			return Class.forName(jp.getText());
		} catch (ClassNotFoundException e) {
			logger.error(
					"Could not deserialize json representation of URI "
							+ jp.getText() + " into URI object because "
							+ e.getMessage(), e);
		}
		return null;
	}

}
