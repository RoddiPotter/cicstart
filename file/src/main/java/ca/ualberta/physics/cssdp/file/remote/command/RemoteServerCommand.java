/* ============================================================
 * RemoteServerCommand.java
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
package ca.ualberta.physics.cssdp.file.remote.command;

import ca.ualberta.physics.cssdp.file.remote.protocol.RemoteConnection;

import com.google.common.base.Strings;

public abstract class RemoteServerCommand<T> {

	private final String hostname;

	private String error;

	private boolean done = false;

	public RemoteServerCommand(String hostname) {
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}

	public abstract void execute(RemoteConnection connection);

	public abstract T getResult();

	public void error(String errorMessage) {
		setError(errorMessage);
	}

	public void setError(String errorMessage) {
		this.error = errorMessage;
	}

	public String getError() {
		return error;
	}

	public boolean hasError() {
		return !Strings.isNullOrEmpty(error);
	}

	public synchronized void setDone(boolean done) {
		this.done = done;
	}

	public synchronized boolean isDone() {
		return done;
	}
}
