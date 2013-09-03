/* ============================================================
 * ServiceResponse.java
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
package ca.ualberta.physics.cssdp.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Joiner;

import ca.ualberta.physics.cssdp.util.CollectionPrinter;

public class ServiceResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum SEVERITY {
		error, warn, info
	}

	private Map<SEVERITY, List<UserMessage>> messages = new ConcurrentHashMap<SEVERITY, List<UserMessage>>();
	private T payload;
	private boolean requestOk = true;

	public ServiceResponse() {
		// default, lazy set params.
	}

	/**
	 * Request is OK with optional payload and optional messages
	 * 
	 * @param payload
	 * @param messages
	 */
	public ServiceResponse(T payload, UserMessage... messages) {
		this(true, payload, messages);
	}

	/**
	 * Request is FAILED with messages
	 * 
	 * @param messages
	 */
	public ServiceResponse(UserMessage... messages) {
		this(false, null, messages);
	}

	// a do all constructor to make this DRY
	private ServiceResponse(boolean ok, T payload, UserMessage... messages) {
		this.requestOk = ok;
		this.payload = payload;
		for (UserMessage message : messages) {
			addMessage(message);
		}
	}

	public void addMessage(UserMessage message) {

		// error messages should default to set the result to not ok
		if (message.getSeverity().equals(SEVERITY.error)) {
			setOk(false);
		}

		if (hasMessages(message.getSeverity())) {
			messages.get(message.getSeverity()).add(message);
		} else {
			messages.put(message.getSeverity(), new ArrayList<UserMessage>(
					Arrays.asList(message)));
		}
	}

	public List<UserMessage> getMessages() {
		List<UserMessage> allMessages = new ArrayList<UserMessage>();
		for (List<UserMessage> eachLevelMessages : messages.values()) {
			allMessages.addAll(eachLevelMessages);
		}
		return allMessages;
	}

	public T getPayload() {
		return payload;
	}

	public boolean hasMessages(SEVERITY errorLevel) {
		return messages.containsKey(errorLevel);
	}

	public boolean isRequestOk() {
		return requestOk;
	}

	public void setOk(boolean ok) {
		requestOk = ok;
	}

	public ServiceResponse<T> setPayload(T payload) {
		this.payload = payload;
		return this;
	}

	public void error(String message) {
		addMessage(new UserMessage(SEVERITY.error, message));
		setOk(false);
	}

	public void info(String message) {
		addMessage(new UserMessage(SEVERITY.info, message));
	}

	public void warn(String message) {
		addMessage(new UserMessage(SEVERITY.warn, message));
	}

	public void info(String message, String... args) {
		addMessage(new UserMessage(SEVERITY.info,
				replaceWithArgs(message, args)));
	}

	public void warn(String message, String... args) {
		addMessage(new UserMessage(SEVERITY.warn,
				replaceWithArgs(message, args)));
	}

	public void error(String message, String... args) {
		addMessage(new UserMessage(SEVERITY.error, replaceWithArgs(message,
				args)));
	}

	/**
	 * Given the message of format 'blah {} blah blah {}, {}' and args String[]
	 * {one, two, three}, the resulting message return will be 'blah one blah
	 * blah two, three'
	 * 
	 * @param message
	 * @param args
	 * @return the formatted string.
	 */
	private String replaceWithArgs(String message, String... args) {
		int i = 0;
		while (message.contains("{}")) {
			message = message.replaceFirst("\\{\\}", args[i]);
			i++;
		}
		return message;
	}

	public void addMessages(List<UserMessage> messages) {
		for (UserMessage message : messages) {
			addMessage(message);
		}
	}

	public void blowupIfError() {

		if (!isRequestOk()) {
			String errorMessages = new CollectionPrinter<UserMessage>(
					getMessages()) {

				@Override
				protected String format(UserMessage t) {
					return t.getMessage();
				}

			}.toString();

			throw new RuntimeException(errorMessages);
		}

	}

	public String getMessagesAsStrings() {
		List<String> messages = new ArrayList<String>();
		for(UserMessage message : getMessages()) {
			messages.add(message.getMessage());
		}
		return Joiner.on("; ").join(messages);
	}

}
