/* ============================================================
 * CommonServletContainer.java
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
package ca.ualberta.physics.cssdp.configuration;

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

import ca.ualberta.physics.cssdp.file.remote.RemoteServers;
import ca.ualberta.physics.cssdp.vfs.configuration.VfsServer;
import ca.ualberta.physics.cssdp.vfs.ftp.VfsFtpServer;
import ca.ualberta.physics.cssdp.vfs.sftp.CssdpFileSystem;
import ca.ualberta.physics.cssdp.vfs.sftp.CssdpPasswordAuthenticator;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Binds the Authorization Server Resources to the JerseryServlet and maps them
 * to the appropriate url mapping
 */
public class CicstartServletContainer extends ServletContainer {

	private static final long serialVersionUID = 1L;

	@Inject
	private RemoteServers remoteServers;
	private Thread remoteServersDaemon;

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ServletException {

		InjectorHolder.inject(this);

		// File server stuff
		remoteServersDaemon = new Thread(remoteServers, "Remote Servers");
		remoteServersDaemon.setDaemon(true);
		remoteServersDaemon.start();

		
		// VFS server stuff
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
	public void destroy() {
		remoteServersDaemon.interrupt();
		super.destroy();
	}
}
