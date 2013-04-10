package ca.ualberta.physics.cssdp.file;

import javax.servlet.ServletException;

import ca.ualberta.physics.cssdp.configuration.CommonServletContainer;
import ca.ualberta.physics.cssdp.file.remote.RemoteServers;

import com.google.inject.Inject;

/**
 * Binds the File Server Resources to the JerseryServlet and maps them to the
 * appropriate url mapping
 */
public class FileServletContainer extends CommonServletContainer {

	private static final long serialVersionUID = 1L;

	@Inject
	private RemoteServers remoteServers;
	private Thread remoteServersDaemon;

	@Override
	protected void touchComponentProperties() {
		FileServer.properties();
	}

	@Override
	public void init() throws ServletException {
		super.init();

		InjectorHolder.inject(this);

		remoteServersDaemon = new Thread(remoteServers, "Remote Servers");
		remoteServersDaemon.setDaemon(true);
		remoteServersDaemon.start();

	}

	@Override
	public void destroy() {
		remoteServersDaemon.interrupt();
		super.destroy();
	}
}
