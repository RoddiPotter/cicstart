/* ============================================================
 * FileConnection.java
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
package ca.ualberta.physics.cssdp.file.remote.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;

/**
 * A FileConnection does not really involve a socket, but it does act like
 * a SocketConnection: you can use a FileSocketConnection to get a listing of a
 * directory, to copy a file from one place to another, etc, all on some
 * "normal" mounted file system, sans socket.
 */
public class FileConnection extends RemoteConnection {

	public FileConnection(Host host) {
		super(host);
	}

	@Override
	public boolean connect() {
		return true; // file connections are always connected.
	}

	@Override
	public boolean disconnect() {
		return true; // and always disconnected too
	}

	@Override
	public boolean isConnected() {
		return true; // file connections are always connected.
	}

	@Override
	public List<RemoteFile> ls(String path) {

		File dir = new File(path);

		File[] files = dir.listFiles();

		List<RemoteFile> list = new ArrayList<RemoteFile>();
		if (files != null) {
			for (File file : files) {

				long size = file.length();
				LocalDateTime modifiedTstamp = new LocalDateTime(file
						.lastModified());
				boolean isDir = file.isDirectory();

				RemoteFile remoteFile = new RemoteFile("file://"
						+ file.getAbsolutePath(), size, modifiedTstamp, isDir);
				list.add(remoteFile);

			}
		}

		return list;
	}

	@Override
	public InputStream download(String source) throws IOException {
		if (source.startsWith("file://")) {
			source = source.replaceAll("file://", "");
		}
		return new FileInputStream(new File(source));
	}

}
