package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.model.Mnemonic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JSONMnemonicDeserializer extends StdDeserializer<Mnemonic> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(JSONMnemonicDeserializer.class);

	public JSONMnemonicDeserializer() {
		super(Mnemonic.class);
	}

	@Override
	public Mnemonic deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			return Mnemonic.of(jp.getText());
		} catch (IllegalArgumentException e) {
			String msg = "Could not deserialize json representation of Mnemonic "
					+ jp.getText()
					+ " into a Mnemonic object because "
					+ e.getMessage();
			logger.error(msg, e);
			throw new JsonParseException(msg, jp.getCurrentLocation());
		}
	}

}
