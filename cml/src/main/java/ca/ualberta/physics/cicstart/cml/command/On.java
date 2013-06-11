package ca.ualberta.physics.cicstart.cml.command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.MacroServer;
import ca.ualberta.physics.cssdp.domain.macro.Instance;

import com.google.common.base.Throwables;

public class On implements Command {

	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	// the host these commands should be run on
	private final String host;
	private final List<CommandDefinition> cmdsToRun;

	public On(String host, List<CommandDefinition> cmdsToRun) {
		this.host = host;
		this.cmdsToRun = cmdsToRun;
	}

	@Override
	public void execute(CMLRuntime runtime) {
		boolean correctServer = false;
		try {
			// localhost and address of spawned server runs the commands
			for (InetAddress inetAddr : InetAddress.getAllByName(InetAddress
					.getLocalHost().getHostName())) {
				if (inetAddr.getHostAddress().equals(host)
						|| inetAddr.getHostName().equals(host)) {
					jobLogger.info("On: running commands for " + host);
					runtime.run(getCmdsToRun());
					correctServer = true;
					break;
				}
			}

			String cicstartServer = MacroServer.properties().getString(
					"cicstart.server.host");

			// we're not on the right host to run commands directly
			if (!correctServer) {

				boolean remoteRequested = false;
				// but we may be on the cicstart server, so request remove VM to
				// run commands
				for (InetAddress inetAddr : InetAddress
						.getAllByName(InetAddress.getLocalHost().getHostName())) {
					if (inetAddr.getHostAddress().equals(cicstartServer)
							|| inetAddr.getHostName().equals(cicstartServer)) {
						jobLogger.info("On: requesting spawned VM " + host
								+ " to run the commands");

						// ssh to host
						Instance instance = runtime.getInstance(host);

						final SSHClient ssh = new SSHClient();
						try {
//							ssh.loadKnownHosts();
							ssh.connect(host);
							try {
								ssh.authPassword("root", instance.password);
								final Session session = ssh.startSession();
								try {
									final net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session
											.exec("ping -c 1 google.com");
									System.out.println(IOUtils.readFully(
											cmd.getInputStream()).toString());
									cmd.join(5, TimeUnit.SECONDS);
									System.out.println("\n** exit status: "
											+ cmd.getExitStatus());
								} finally {
									session.close();
								}
							} finally {
								ssh.disconnect();
							}
						} catch (Exception e) {
							Throwables.propagate(e);
						}

						remoteRequested = true;
						break;
					}
				}

				// we're on the wrong VM so don't run anything
				if (!remoteRequested) {
					InetAddress localhost;
					localhost = InetAddress.getLocalHost();
					String localIpAddress = localhost.getHostAddress();
					String localHostName = localhost.getHostName();
					jobLogger.info("On: skipping -> This is " + localIpAddress
							+ "(" + localHostName + ")"
							+ " but these cmds are for " + host);
				}
			}

		} catch (UnknownHostException e) {
			Throwables.propagate(e);
		}

	}

	@Override
	public Object getResult() {
		return null;
	}

	public List<CommandDefinition> getCmdsToRun() {
		return cmdsToRun;
	}

}
