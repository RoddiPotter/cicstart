package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.model.Point;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JSONPointDeserializer extends StdDeserializer<Point> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(JSONPointDeserializer.class);

	public JSONPointDeserializer() {
		super(Point.class);
	}

	@Override
	public Point deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			return new Point(jp.getText());
		} catch (IllegalArgumentException e) {
			String msg = "Could not deserialize json representation of Point "
					+ jp.getText() + " into a Point object because "
					+ e.getMessage();
			logger.error(msg, e);
			throw new JsonParseException(msg, jp.getCurrentLocation());
		}
	}

}
