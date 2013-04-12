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

				// if (!file.isFile() && !file.isDirectory()) {
				//
				// // how can an entry not be a file or a directory?
				// // well, many reasons ... on unix this could be a socket
				// // or a named pipe or a symbolic link.
				// info.setIsFile(null);
				//
				// } else {
				//
				// if (!file.canRead() || isSymLink(file)) {
				// // no permission to read this file or dir... essentially skip
				// it.
				// logger.debug("No permission to read " +
				// file.getAbsolutePath());
				// info.setIsFile(null);
				// } else {
				// info.setIsFile(file.isFile() && !file.isDirectory());
				// }
				// }
				//
				// info.setLastmodifiedMillis(file.lastModified());
				// info.setFileSizeInBytes(file.length());

				RemoteFile remoteFile = new RemoteFile("file://"
						+ file.getAbsolutePath(), size, modifiedTstamp, isDir);
				list.add(remoteFile);

			}
		}

		return list;
	}

//	/**
//	 * Tells if a file is a symbolic link or not. Uses a similar algorithm to
//	 * Apache Commons for symbolic link determination.
//	 * 
//	 * @param file
//	 *            the File
//	 * @return true if the file is a symbolic link, false otherwise.
//	 */
//	private boolean isSymLink(File file) {
//
//		try {
//			// technique taken from Apache Commons to determine if a file is a
//			// symbolic link or not.
//
//			File canon;
//			if (file.getParent() == null) {
//				canon = file;
//			} else {
//				File canonDir = null;
//				canonDir = file.getParentFile().getCanonicalFile();
//				canon = new File(canonDir, file.getName());
//			}
//			return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
//
//		} catch (IOException e) {
//			throw new ProtocolException(
//					"Can't find file to tell if it's a symbolic link", false, e);
//		}
//	}

	@Override
	public InputStream download(String source) throws IOException {
		if (source.startsWith("file://")) {
			source = source.replaceAll("file://", "");
		}
		return new FileInputStream(new File(source));
	}

	// /**
	// * Copy the source file to the destination.
	// */
	// @Override
	// public void transfer(Url source, Url destination, final ProgressMonitor
	// monitor) {
	//
	// File f1 = new File(source.getPath());
	// File f2 = new File(destination.getPath());
	// if (f1 != null && f2 != null) {
	// logger.debug("Will transfer " + f1.getAbsolutePath() + " to " +
	// f2.getAbsolutePath());
	// } else {
	// throw new ProtocolException("Can't find one of these: " +
	// f1.getAbsolutePath() + " or "
	// + f2.getAbsolutePath(), CAUSE.resource_not_found, false);
	// }
	// InputStream in = null;
	// OutputStream out = null;
	// try {
	// in = new FileInputStream(f1);
	//
	// f2.getParentFile().mkdirs();
	// f2.createNewFile();
	//
	// // always overwrite
	// out = new FileOutputStream(f2);
	//
	// byte[] buf = new byte[8192];
	// int len;
	// while ((len = in.read(buf)) > 0) {
	// out.write(buf, 0, len);
	// monitor.onStatusUpdate(len);
	// }
	// destination.setFileSizeInBytes(f2.length());
	// } catch (FileNotFoundException e) {
	// throw new ProtocolException(
	// "No file found of either: " + source.toString() + " or " +
	// destination.toString(),
	// CAUSE.resource_not_found, false, e);
	// } catch (IOException e) {
	// throw new ProtocolException("copy failed from " + source.toString() +
	// " to " + destination.toString(),
	// CAUSE.unknown, false, e);
	// } finally {
	//
	// try {
	// if (in != null) {
	// in.close();
	// }
	// if (out != null) {
	// out.close();
	// }
	// } catch (IOException e) {
	// // ignore.
	// }
	// }
	// }
}
