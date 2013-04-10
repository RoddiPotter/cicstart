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
