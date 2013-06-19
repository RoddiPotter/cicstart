package ca.ualberta.physics.cicstart.macro.resource;

import java.io.File;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.MacroServer;

public class SshTests {

	// assumes you have an accessible image running
	// this is for learning how to use the jsch client with keypairs

	@Test
	public void testSshAccessCode() throws Exception {

		final SSHClient client = new SSHClient();
		client.addHostKeyVerifier(new PromiscuousVerifier());
		try {
			client.connect("208.75.74.81");
			KeyProvider keys = client.loadKeys(new File(MacroServer
					.properties().getString("cicstart.pemfile")).getPath());
			client.authPublickey("ubuntu", keys);
			final Session session = client.startSession();
			try {
				final net.schmizz.sshj.connection.channel.direct.Session.Command cmd = session
						.exec("ping -c 1 google.com");
				System.out.println(IOUtils.readFully(cmd.getInputStream())
						.toString());
				cmd.join(5, TimeUnit.SECONDS);
				System.out.println("\n** exit status: " + cmd.getExitStatus());
			} finally {
				session.close();
			}
		} finally {
			client.disconnect();
		}

	}

}
