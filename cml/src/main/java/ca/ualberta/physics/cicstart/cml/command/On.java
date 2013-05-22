package ca.ualberta.physics.cicstart.cml.command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

			if (!correctServer) {
				InetAddress localhost;
				localhost = InetAddress.getLocalHost();
				String localIpAddress = localhost.getHostAddress();
				String localHostName = localhost.getHostName();
				jobLogger.info("On: skipping -> This is " + localIpAddress
						+ "(" + localHostName + ")"
						+ " but these cmds are for " + host);
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
