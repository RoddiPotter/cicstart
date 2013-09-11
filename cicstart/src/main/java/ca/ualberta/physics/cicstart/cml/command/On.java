package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.util.NetworkUtil;

import com.google.common.net.InetAddresses;

public class On implements Command {

	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	// the host these commands should be run on
	private final String host;
	private final String serverVar;
	private final List<CommandDefinition> cmdsToRun;
	private final String script;

	private int maxRetries = 10;
	private int retryCount = 0;

	public On(Instance instance, List<CommandDefinition> cmdsToRun,
			String script, String serverVar) {
		this.host = instance.ipAddress;
		this.serverVar = serverVar;
		this.cmdsToRun = cmdsToRun;
		this.script = script;
	}

	@Override
	public void execute(CMLRuntime runtime) {

		MDC.put("JobId", runtime.getRequestId());
		
		jobLogger.info("This is try # " + retryCount + " of " + maxRetries);

		String cicstartServer = MacroServer.properties().getString(
				"cicstart.server.internal");

		if (NetworkUtil.currentlyRunningOn(host)) {
			// we're actually logged into the spawned VM so just run the
			// commands
			jobLogger.info("On: running commands on " + host);
			runtime.run(getCmdsToRun());

		} else if (NetworkUtil.currentlyRunningOn(cicstartServer)) {
			// we're on the CICSTART server so do a remote SSH to get the
			// binary client on the spawned VM and run it

			jobLogger.info("On: connecting to remote " + host
					+ " to setup client.");

			SSHClient client = new SSHClient();
			client.addHostKeyVerifier(new PromiscuousVerifier());
			try {

				try {

					client.connect(InetAddresses.forString(host));
					String privateKeyFile = MacroServer.properties().getString(
							"cicstart.pemfile");
					File pemFile = new File(privateKeyFile);
					jobLogger.info("Connecting to " + host + " using keyfile "
							+ pemFile.getAbsolutePath() + " and user 'ubuntu'");
					KeyProvider keys = client.loadKeys(pemFile.getPath());
					client.authPublickey("ubuntu", keys);

					runOnRemote(client, "sudo apt-get -y update --fix-missing");
					runOnRemote(client, "sudo apt-get -y install openjdk-6-jre");

					// the common properties will be overridden when the client
					// is built if the jvm building the client can't access the
					// internal ip of the cicstart server
					String macroUrl = Common.properties().getString("api.url") + "/macro"
							+ "/macro/bin?include_jre=false&use_internal_network=true&job_id="
							+ runtime.getRequestId();

					// bootstrap the script, removing references to vm created
					// and forcing it to use the ip defined by the on command
					String bootstrappedScript = script.replaceAll("\\$"
							+ serverVar, "\"" + host + "\"");

					runOnRemote(
							client,
							"curl -H CICSTART.session:\""
									+ runtime.getCICSTARTSession()
									+ "\" -H Content-Type:\"application/octet-stream\" --data-binary "
									+ "'" + bootstrappedScript + "'"
									+ " -X POST " + "\"" + macroUrl + "\""
									+ "> client.tar.gz");

					runOnRemote(client, "tar zxvf client.tar.gz");
					runOnRemote(client, "cd bin && ./run");

				} finally {
					client.disconnect();
					client.close();
				}

			} catch (Exception e) {

				jobLogger.warn("something when wrong connecting to spawned vm", e);
				// assigned external address takes a few seconds and is
				// non-determinant of what exception will be thrown so just
				// retry until we can connect to the host
				if (retryCount < maxRetries) {
					retryCount++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO handle interrupt.
					}

					execute(runtime);
				}
			}

		} else {

			// we're somewhere else, don't run anything.
			InetAddress localhost;
			String localHostName;
			String localIpAddress;
			try {
				localhost = InetAddress.getLocalHost();
				localIpAddress = localhost.getHostAddress();
				localHostName = localhost.getHostName();
			} catch (UnknownHostException ignore) {
				localIpAddress = "unknown";
				localHostName = "unknown";
			}
			jobLogger.info("On: skipping -> This is " + localIpAddress + " ("
					+ localHostName + ")" + " but these cmds are for " + host);

		}

	}

	private void runOnRemote(SSHClient client, String command)
			throws ConnectionException, TransportException, IOException {

		Session session = client.startSession();
		try {
			jobLogger.info("Running '" + command + "' on "
					+ client.getRemoteAddress().getHostAddress());
			net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session
					.exec(command);
			jobLogger.info("On (" + client.getRemoteAddress().getHostAddress()
					+ "): STDOUT: "
					+ IOUtils.readFully(cmd.getInputStream()).toString());
			cmd.join(15, TimeUnit.SECONDS);
			jobLogger.info("On (" + client.getRemoteAddress().getHostAddress()
					+ "): exit status: " + cmd.getExitStatus());
		} catch (ConnectionException ce) {
			jobLogger.error("Timed out completing command on " + host, ce);
		} finally {
			session.close();
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
