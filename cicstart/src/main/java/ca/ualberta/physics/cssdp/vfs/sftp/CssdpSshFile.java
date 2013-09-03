/* ============================================================
 * CssdpSshFile.java
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
package ca.ualberta.physics.cssdp.vfs.sftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.server.SshFile;

public class CssdpSshFile implements SshFile {

	private final File root;
	private File file;

	public CssdpSshFile(File root, String path) {
		
		this.root = root;

		if (path.equals("/")) {
			file = root;
		} else if (!path.equals("..") && !path.equals(".")) {
			file = new File(path);
		} else {
			file = new File(root, path);
		}
	}

	@Override
	public boolean create() throws IOException {
		return file.createNewFile();
	}

	@Override
	public InputStream createInputStream(long offset) throws IOException {
		// permission check
		if (!isReadable()) {
			throw new IOException("No read permission : " + file.getName());
		}

		// move to the appropriate offset and create input stream
		final RandomAccessFile raf = new RandomAccessFile(file, "r");
		raf.seek(offset);

		// The IBM jre needs to have both the stream and the random access file
		// objects closed to actually close the file
		return new FileInputStream(raf.getFD()) {
			public void close() throws IOException {
				super.close();
				raf.close();
			}
		};
	}

	@Override
	public OutputStream createOutputStream(long offset) throws IOException {
		// permission check
		if (!isWritable()) {
			throw new IOException("No write permission : " + file.getName());
		}

		// create output stream
		final RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.setLength(offset);
		raf.seek(offset);

		// The IBM jre needs to have both the stream and the random access file
		// objects closed to actually close the file
		return new FileOutputStream(raf.getFD()) {
			public void close() throws IOException {
				super.close();
				raf.close();
			}
		};

	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public boolean doesExist() {
		return file.exists();
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@Override
	public long getLastModified() {
		return file.lastModified();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public SshFile getParentFile() {

		if (root.equals(file)) {
			return new CssdpSshFile(root, ".");
		} else {
			return new CssdpSshFile(root, file.getParent());
		}
	}

	@Override
	public long getSize() {
		return file.length();
	}

	@Override
	public void handleClose() throws IOException {
		// noop
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public boolean isExecutable() {
		return file.canExecute();
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isReadable() {
		return file.canRead();
	}

	@Override
	public boolean isRemovable() {

		// root cannot be deleted
		if (root.equals(file)) {
			return false;
		}

		return file.canWrite();
	}

	@Override
	public boolean isWritable() {
		return file.canWrite();
	}

	@Override
	public List<SshFile> listSshFiles() {

		List<SshFile> sshFiles = new ArrayList<SshFile>();
		if (file.getPath().startsWith(root.getAbsolutePath())) {

			File[] files = file.listFiles();
			for (File file : files) {
				sshFiles.add(new CssdpSshFile(root, file.getPath()));
			}

		} else {

			File[] files = root.listFiles();
			for (File file : files) {
				sshFiles.add(new CssdpSshFile(root, file.getAbsolutePath()));
			}

		}

		return sshFiles;
	}

	@Override
	public boolean mkdir() {
		boolean retVal = false;
		if (isWritable()) {
			retVal = file.mkdir();
		}
		return retVal;

	}

	@Override
	public boolean move(SshFile dest) {
		boolean retVal = false;
		if (dest.isWritable() && isReadable()) {
			File destFile = ((CssdpSshFile) dest).file;

			if (destFile.exists()) {
				// renameTo behaves differently on different platforms
				// this check verifies that if the destination already exists,
				// we fail
				retVal = false;
			} else {
				retVal = file.renameTo(destFile);
			}
		}
		return retVal;
	}

	@Override
	public boolean setLastModified(long time) {
		return file.setLastModified(time);
	}

	@Override
	public void truncate() throws IOException {
		RandomAccessFile tempFile = new RandomAccessFile(file, "rw");
		tempFile.setLength(0);
		tempFile.close();
	}

	@Override
	public String getOwner() {
		return null;
	}

}
