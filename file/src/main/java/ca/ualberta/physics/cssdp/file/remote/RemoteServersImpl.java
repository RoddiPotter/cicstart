/* ============================================================
 * RemoteServersImpl.java
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
package ca.ualberta.physics.cssdp.file.remote;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.InjectorHolder;
import ca.ualberta.physics.cssdp.file.dao.HostEntryDao;
import ca.ualberta.physics.cssdp.file.remote.command.RemoteServerCommand;
import ca.ualberta.physics.cssdp.file.remote.protocol.RemoteConnection;

import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * Handles communication
 * 
 */
public class RemoteServersImpl implements RemoteServers {

	private static final Logger logger = LoggerFactory
			.getLogger(RemoteServersImpl.class);

	@Inject
	private HostEntryDao hostEntryDao;

	private final ConcurrentMap<String, BlockingQueue<RemoteConnection>> connectionPools = new ConcurrentHashMap<String, BlockingQueue<RemoteConnection>>();

	private final BlockingQueue<RemoteServerCommand<?>> backlog = new ArrayBlockingQueue<RemoteServerCommand<?>>(
			5000);

	private final CopyOnWriteArraySet<RemoteServerCommand<?>> currentRequests = new CopyOnWriteArraySet<RemoteServerCommand<?>>();

	public RemoteServersImpl() {
		InjectorHolder.inject(this);

		// initialize the existing hosts in this remove server object
		List<Host> hostEntries = hostEntryDao.list();
		for (Host hostEntry : hostEntries) {
			add(hostEntry);
		}

	}

	@Override
	public void requestOperation(RemoteServerCommand<?> command) {

		if(!currentRequests.contains(command)) {
			
			currentRequests.add(command);
			
			// verify the remove servers are configured with the host in the command
			if (connectionPools.containsKey(command.getHostname()) == false) {
				throw new IllegalArgumentException(
						"Remote Servers needs to be configured with "
								+ command.getHostname()
								+ " before this action can be performend.  Add the host and then retry the operation.");
			}

			try {
				this.backlog.offer(command, 30, TimeUnit.SECONDS);
				
			} catch (InterruptedException e) {
				throw new RuntimeException(
						"File Service is too busy, please try again soon.");
			}
		}
		

	}

	@Override
	public void run() {

		logger.info("Starting Remote Server Proxy thread");

		try {

			while (true) {

				RemoteServerCommand<?> command = backlog.take();

				BlockingQueue<RemoteConnection> connectionPool = connectionPools
						.get(command.getHostname());

				if (connectionPool == null) {
					command.error("Host has not been configured in RemoteServers yet.  Add and retry.");
					command.setDone(true);
				} else {

					RemoteConnection conn = connectionPool.poll(1,
							TimeUnit.MINUTES);

					if (conn != null) {

						logger.info("Got a connection for "
								+ command.getHostname() + " to run command "
								+ command.getClass().getSimpleName());

						Thread commandRunner = new Thread(new CommandRunner(
								conn, command));
						commandRunner.start();

					} else {
						logger.warn(command.getHostname()
								+ " is too busy, no available "
								+ "connections at this time.  Try again later.");
					}
				}
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.info("I've been interrupted");
		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(Throwables
					.getRootCause(e)));
		}

	}

	private void returnConnection(String hostname, RemoteConnection conn) {
		if (conn == null) {
			throw new IllegalStateException(
					"Can't add a null connection back to the pool!");
		}
		BlockingQueue<RemoteConnection> connectionPool = connectionPools
				.get(hostname);
		connectionPool.offer(conn);
	}

	private class CommandRunner implements Runnable {

		private final RemoteConnection connection;
		private final RemoteServerCommand<?> command;

		public CommandRunner(RemoteConnection connection,
				RemoteServerCommand<?> command) {
			this.connection = connection;
			this.command = command;
		}

		public void run() {
			try {
				logger.info("Starting command " + command.getClass().getSimpleName() + " with " + command.getHostname());
				command.execute(connection);
			} finally {
				currentRequests.remove(command);
				returnConnection(command.getHostname(), connection);
				logger.info("Finished command " + command.getClass().getSimpleName() + " with " + command.getHostname());
			}
		}

	}

	@Override
	public void remove(Host host) {

		ArrayBlockingQueue<RemoteConnection> connectionPool = (ArrayBlockingQueue<RemoteConnection>) connectionPools
				.remove(host.getHostname());
		connectionPool.clear();

	}

	@Override
	public boolean contains(Host hostEntry) {
		return connectionPools.containsKey(hostEntry.getHostname());
	}

	@Override
	public void add(Host hostEntry) {

		ArrayBlockingQueue<RemoteConnection> connectionPool = new ArrayBlockingQueue<RemoteConnection>(
				hostEntry.getMaxConnections());

		logger.info("Initializing connection pool for " + hostEntry
				+ ". Max connections = " + hostEntry.getMaxConnections());

		for (int i = 0; i < hostEntry.getMaxConnections(); i++) {
			connectionPool.add(RemoteConnection.newInstance(hostEntry));
		}
		connectionPools.putIfAbsent(hostEntry.getHostname(), connectionPool);

	}

}
