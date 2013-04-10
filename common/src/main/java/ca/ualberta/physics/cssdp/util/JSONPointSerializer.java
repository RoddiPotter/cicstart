package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import ca.ualberta.physics.cssdp.model.Point;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Marshals LocalDates back and forth between String and LocalDate
 * representations.
 */
public class JSONPointSerializer extends StdSerializer<Point> {

	public JSONPointSerializer() {
		super(Point.class, false);
	}

	@Override
	public void serialize(Point value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeString(value.toASCIIString());
	}

}
