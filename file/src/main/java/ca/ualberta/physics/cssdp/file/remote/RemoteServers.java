package ca.ualberta.physics.cssdp.file.remote;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.remote.command.CommandRequest;
import ca.ualberta.physics.cssdp.file.remote.command.RemoteServerCommand;

public interface RemoteServers extends Runnable {

	public int requestOperation(RemoteServerCommand<?> command);
	public CommandRequest getRequest(Integer requestId);
	public void remove(Host he);
	public boolean contains(Host hostEntry);
	public void add(Host hostEntry);
	
}