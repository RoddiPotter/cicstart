package ca.ualberta.physics.cssdp.vfs;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.sftp.SftpSubsystem;

import ca.ualberta.physics.cssdp.configuration.CommonServletContainer;
import ca.ualberta.physics.cssdp.vfs.ftp.VfsFtpServer;
import ca.ualberta.physics.cssdp.vfs.sftp.CssdpFileSystem;
import ca.ualberta.physics.cssdp.vfs.sftp.CssdpPasswordAuthenticator;

import com.google.common.base.Throwables;

public class VfsServletContainer extends CommonServletContainer {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ServletException {

		// start the FTP server
		VfsFtpServer.main(new String[0]);

		// start the SFTP server
		String pemKeyFile = VfsServer.properties().getString("pemKeyFile");
		int sftpPort = VfsServer.properties().getInt("sftpPort");

		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(sftpPort);
		sshd.setKeyPairProvider(new FileKeyPairProvider(
				new String[] { pemKeyFile }));
		sshd.setSubsystemFactories(Arrays
				.<NamedFactory<Command>> asList(new SftpSubsystem.Factory()));
		sshd.setFileSystemFactory(new FileSystemFactory() {

			@Override
			public FileSystemView createFileSystemView(Session session)
					throws IOException {

				return new CssdpFileSystem(session.getUsername());
			}
		});
		sshd.setPasswordAuthenticator(new CssdpPasswordAuthenticator());
		try {
			sshd.start();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}

		super.init();

	}

	@Override
	protected void touchComponentProperties() {
		VfsServer.properties();
	}

}
