/* ============================================================
 * VfsFtpServer.java
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
package ca.ualberta.physics.cssdp.vfs.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.vfs.VfsServer;

import com.google.common.base.Throwables;

public class VfsFtpServer {

	private static final Logger logger = LoggerFactory
			.getLogger(VfsFtpServer.class);

	private static FtpServer server;

	public VfsFtpServer() {

	}

	public static void main(String[] args) {
		
		FtpServerFactory serverFactory = new FtpServerFactory();
		CommandFactoryFactory commandFactoryFactory = new CommandFactoryFactory();
		CommandFactory commandFactory = commandFactoryFactory.createCommandFactory();
		serverFactory.setCommandFactory(commandFactory);
		ListenerFactory factory = new ListenerFactory();
		// set the port of the listener

		int ftpPort = VfsServer.properties().getInt("ftpPort");
		factory.setPort(ftpPort);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		serverFactory.getFtplets().put("default", new VfsFtplet());
		serverFactory.setUserManager(new VfsFtpUserManager());
		
		server = serverFactory.createServer();
		// start the server
		try {
			server.start();
		} catch (FtpException e) {
			logger.error("Could not start Mina FTP Server", e);
			Throwables.propagate(e);
		}
	}

}
