package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Marshals LocalDates back and forth between String and LocalDate representations.
 */
public class JSONClassSerializer extends StdSerializer<Class<?>> {

	public JSONClassSerializer() {
		super(Class.class, false);
	}

	@Override
	public void serialize(Class<?> value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeString(value.getName());
	}

}
