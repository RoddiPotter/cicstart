package ca.ualberta.physics.cssdp.service;

import java.io.Serializable;

import ca.ualberta.physics.cssdp.service.ServiceResponse.SEVERITY;

public class UserMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private final SEVERITY level;
	private final String message;

	public UserMessage(SEVERITY level, String message) {
		this.level = level;
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.coderod.condo.service.UserMessage#getSeverity()
	 */
	public SEVERITY getSeverity() {
		return level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.coderod.condo.service.UserMessage#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return level.name() + ": " + message;
	}

}