package ca.ualberta.physics.cssdp.util;

import java.io.IOException;

import ca.ualberta.physics.cssdp.model.Mnemonic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Marshals LocalDates back and forth between String and LocalDate
 * representations.
 */
public class JSONMnemonicSerializer extends StdSerializer<Mnemonic> {

	public JSONMnemonicSerializer() {
		super(Mnemonic.class, false);
	}

	@Override
	public void serialize(Mnemonic value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeString(value.getValue());
	}

}
