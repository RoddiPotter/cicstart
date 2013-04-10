package ca.ualberta.physics.cssdp.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JSONURIDeserializer extends StdDeserializer<URI> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(JSONURIDeserializer.class);

	public JSONURIDeserializer() {
		super(URI.class);
	}

	@Override
	public URI deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		try {
			return new URI(jp.getText());
		} catch (URISyntaxException e) {
			logger.error("Could not deserialize json representation of URI " + jp.getText()
					+ " into URI object because " + e.getMessage(), e);
		}
		return null;
	}

}
