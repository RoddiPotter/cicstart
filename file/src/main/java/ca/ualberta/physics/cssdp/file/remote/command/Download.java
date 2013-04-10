package ca.ualberta.physics.cssdp.file.remote.command;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.file.remote.protocol.FtpConnection;
import ca.ualberta.physics.cssdp.file.remote.protocol.RemoteConnection;
import ca.ualberta.physics.cssdp.file.service.CacheService;
import ca.ualberta.physics.cssdp.util.UrlParser;

import com.google.common.base.Throwables;

public class Download extends RemoteServerCommand<Void> {

	private static final Logger logger = LoggerFactory
			.getLogger(Download.class);

	private final CacheService fileCache;
	private final String url;

	public Download(CacheService fileCache, String hostname, String url) {
		super(hostname);
		this.fileCache = fileCache;
		this.url = url;
	}

	@Override
	public void execute(RemoteConnection connection) {

		InputStream inputStream = null;
		try {
			connection.connect();

			String filename = UrlParser.getLeaf(url);
			inputStream = connection.download(url);
			fileCache.put(filename, url, inputStream);

		} catch (Exception e) {
			logger.error("Download failed", e);
			error(Throwables.getRootCause(e).getMessage());
		} finally {
			try {
				inputStream.close();
				if(connection.getClass().equals(FtpConnection.class)) {
					((FtpConnection)connection).nudge();
				}
			} catch (IOException ignore) {
			}
			connection.disconnect();
		}

	}

	@Override
	public Void getResult() {
		return null;
	}

}
