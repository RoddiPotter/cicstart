/* ============================================================
 * JSONObjectMapperProvider.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.configuration;

import java.net.URI;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.model.Point;
import ca.ualberta.physics.cssdp.util.JSONClassDeserializer;
import ca.ualberta.physics.cssdp.util.JSONClassSerializer;
import ca.ualberta.physics.cssdp.util.JSONLocalDateDeserializer;
import ca.ualberta.physics.cssdp.util.JSONLocalDateSerializer;
import ca.ualberta.physics.cssdp.util.JSONLocalDateTimeDeserializer;
import ca.ualberta.physics.cssdp.util.JSONLocalDateTimeSerializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;
import ca.ualberta.physics.cssdp.util.JSONPointDeserializer;
import ca.ualberta.physics.cssdp.util.JSONPointSerializer;
import ca.ualberta.physics.cssdp.util.JSONURIDeserializer;
import ca.ualberta.physics.cssdp.util.JSONURISerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Provider;

public class JSONObjectMapperProvider implements Provider<ObjectMapper> {

	private final ObjectMapper mapper;

	public JSONObjectMapperProvider() {

		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);

		SimpleModule module = new SimpleModule("cicstart");

		module.addSerializer(LocalDate.class, new JSONLocalDateSerializer());
		module.addDeserializer(LocalDate.class, new JSONLocalDateDeserializer());

		module.addSerializer(LocalDateTime.class,
				new JSONLocalDateTimeSerializer());
		module.addDeserializer(LocalDateTime.class,
				new JSONLocalDateTimeDeserializer());

		module.addSerializer(URI.class, new JSONURISerializer());
		module.addDeserializer(URI.class, new JSONURIDeserializer());

		module.addSerializer(new JSONClassSerializer());
		module.addDeserializer(Class.class, new JSONClassDeserializer());

		module.addSerializer(new JSONMnemonicSerializer());
		module.addDeserializer(Mnemonic.class, new JSONMnemonicDeserializer());

		module.addSerializer(new JSONPointSerializer());
		module.addDeserializer(Point.class, new JSONPointDeserializer());

		mapper.registerModule(module);

	}

	@Override
	public ObjectMapper get() {
		return mapper;
	}

}
