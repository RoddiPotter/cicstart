/* ============================================================
 * UserMessage.java
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