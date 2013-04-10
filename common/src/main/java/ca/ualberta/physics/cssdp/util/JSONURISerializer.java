package ca.ualberta.physics.cssdp.util;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Marshals LocalDates back and forth between String and LocalDate representations.
 */
public class JSONURISerializer extends StdSerializer<URI> {

	public JSONURISerializer() {
		super(URI.class);
	}

	@Override
	public void serialize(URI value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeString(value.toASCIIString());
	}

}
