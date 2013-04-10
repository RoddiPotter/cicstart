package ca.ualberta.physics.cssdp.file.remote.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.Host.Protocol;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;

/**
 * A RemoteConnection is simply a connection to a host, using a protocol of some
 * type.
 */
public abstract class RemoteConnection {

	/**
	 * Simple factory method
	 * 
	 * @param hostEntry
	 * @return
	 */
	public static RemoteConnection newInstance(Host hostEntry) {
		Protocol protocol = hostEntry.getProtocol();

		switch (protocol) {
		case file:
			return new FileConnection(hostEntry);
		case ftp:
			return new FtpConnection(hostEntry);
		case sftp:
			return new SftpConnection(hostEntry);
		case ftps:
			return new FtpsConnection(hostEntry);
		default:
			new IllegalArgumentException("Don't know how to handle protocol: "
					+ protocol);
		}

		return null;

	}

	private final Host hostEntry;

	public RemoteConnection(Host hostEntry) {
		this.hostEntry = hostEntry;
	}

	public Host getHostEntry() {
		return hostEntry;
	}

	public boolean test() {
		connect();
		return isConnected();
	}

	public abstract boolean connect();

	public abstract boolean disconnect();

	public abstract boolean isConnected();

	public abstract InputStream download(String source) throws IOException;

	public abstract List<RemoteFile> ls(String dir);
}
