package ca.ualberta.physics.cssdp.file.remote;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.InjectorHolder;
import ca.ualberta.physics.cssdp.file.dao.HostEntryDao;
import ca.ualberta.physics.cssdp.file.remote.command.CommandRequest;
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

	private final AtomicInteger requestIdGenerator = new AtomicInteger();

	private final ConcurrentMap<String, BlockingQueue<RemoteConnection>> connectionPools = new ConcurrentHashMap<String, BlockingQueue<RemoteConnection>>();

	private final BlockingQueue<RemoteServerCommand<?>> backlog = new ArrayBlockingQueue<RemoteServerCommand<?>>(
			5000);

	private final ConcurrentMap<Integer, CommandRequest> requests = new ConcurrentHashMap<Integer, CommandRequest>();

	public RemoteServersImpl() {
		InjectorHolder.inject(this);

		// initialize the existing hosts in this remove server object
		List<Host> hostEntries = hostEntryDao.list();
		for (Host hostEntry : hostEntries) {
			add(hostEntry);
		}

	}

	@Override
	public int requestOperation(RemoteServerCommand<?> command) {

		// verify the remove servers are configured with the host in the command
		if (connectionPools.containsKey(command.getHostname()) == false) {
			throw new IllegalArgumentException(
					"Remote Servers needs to be configured with "
							+ command.getHostname()
							+ " before this action can be performend.  Add the host and then retry the operation.");
		}

		int requestId = requestIdGenerator.incrementAndGet();
		try {
			this.backlog.offer(command, 30, TimeUnit.SECONDS);
			requests.put(requestId, new CommandRequest(requestId, command));
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"File Service is too busy, please try again soon.");
		}
		return requestId;

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
				command.execute(connection);
			} finally {
				returnConnection(command.getHostname(), connection);
			}
		}

	}

	@Override
	public CommandRequest getRequest(Integer requestId) {
		return requests.get(requestId);
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
