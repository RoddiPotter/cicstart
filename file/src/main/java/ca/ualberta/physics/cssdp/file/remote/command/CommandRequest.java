package ca.ualberta.physics.cssdp.file.remote.command;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class CommandRequest {

	private final int requestId;
	private final RemoteServerCommand<?> command;
	private final LocalDateTime requestTime = new LocalDateTime(DateTimeZone.forID("America/Edmonton"));
	
	public CommandRequest(int requestId, RemoteServerCommand<?> command) {
		this.requestId = requestId;
		this.command = command;
	}

	public int getRequestId() {
		return requestId;
	}

	public RemoteServerCommand<?> getCommand() {
		return command;
	}

	public LocalDateTime getRequestTime() {
		return requestTime;
	}
	
}
