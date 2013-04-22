/* ============================================================
 * Download.java
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
package ca.ualberta.physics.cssdp.file.remote.command;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.file.remote.protocol.FtpConnection;
import ca.ualberta.physics.cssdp.file.remote.protocol.RemoteConnection;
import ca.ualberta.physics.cssdp.file.service.CacheService;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.util.UrlParser;

import com.google.common.base.Throwables;

public class Download extends RemoteServerCommand<Void> {

	private static final long serialVersionUID = 1L;

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
			ServiceResponse<String> sr = fileCache.put(filename, url,
					inputStream);
			if (sr.isRequestOk()) {
				logger.info("Added " + filename + " to file cache with MD5 "
						+ sr.getPayload() + ", originating from " + url);
			} else {
				logger.error("Cache put failed because "
						+ sr.getMessagesAsStrings());
			}
		} catch (Exception e) {
			logger.error("Download failed", e);
			error(Throwables.getRootCause(e).getMessage());
		} finally {
			try {
				inputStream.close();
				if (connection.getClass().equals(FtpConnection.class)) {
					((FtpConnection) connection).nudge();
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

	@Override
	public String _pk() {
		return url;
	}

}
