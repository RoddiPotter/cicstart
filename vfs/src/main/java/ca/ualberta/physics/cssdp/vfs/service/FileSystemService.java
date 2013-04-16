/* ============================================================
 * FileSystemService.java
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
package ca.ualberta.physics.cssdp.vfs.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.util.StorageUnit;
import ca.ualberta.physics.cssdp.vfs.VfsServer;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class FileSystemService {

	public ServiceResponse<Void> write(final Long owner, final String path,
			final String filename, final InputStream fileData) {

		final ServiceResponse<Void> sr = new ServiceResponse<Void>();

		Long defaultQuota = VfsServer.properties()
				.getLong("default_user_quota");

		File userDir = getUserRoot(owner);

		long diskUsage = sizeOf(userDir);
		if (diskUsage > defaultQuota) {
			sr.error("Can not save file to VFS, user quota exceeded ("
					+ StorageUnit.GIGABYTE.format(defaultQuota));
			return sr;
		}

		File file = new File(new File(userDir, path), filename);
		if (file.exists()) {
			sr.error("A file already exists at " + path + "/" + filename);
			return sr;
		}
		try {
			Files.createParentDirs(file);

			Files.copy(new InputSupplier<InputStream>() {

				@Override
				public InputStream getInput() throws IOException {
					return fileData;
				}
			}, file);
		} catch (IOException e) {
			sr.error("Could not write file data because " + e.getMessage());
		}
		return sr;
	}

	private File getUserRoot(final Long owner) {
		File vfsRoot = new File(VfsServer.properties().getString("vfs_root"));
		File userDir = new File(vfsRoot, owner.toString());
		return userDir;
	}

	private long sizeOf(File userDir) {
		FileFilter fileFilter = new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.getName().equals(".")
						&& file.getName().equals("..");
			}

		};

		long size = 0L;
		File[] files = userDir.listFiles(fileFilter);
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					size += file.length();
				} else {
					size += sizeOf(file);
				}
			}
		}
		return size;
	}

	public ServiceResponse<File> read(Long owner, String path) {

		ServiceResponse<File> sr = new ServiceResponse<File>();

		File userDir = getUserRoot(owner);

		File file = new File(userDir, path);
		if (file.exists()) {
			sr.setPayload(file);
		}

		return sr;
	}

	public ServiceResponse<File[]> ls(Long owner, String path) {

		ServiceResponse<File[]> sr = new ServiceResponse<File[]>();

		File userDir = getUserRoot(owner);
		File dir = new File(userDir, path);
		if (dir.exists()) {
			sr.setPayload(dir.listFiles());
		}
		return sr;
	}

	public ServiceResponse<Void> rm(Long owner, String path) {

		ServiceResponse<Void> sr = new ServiceResponse<Void>();
		File userDir = getUserRoot(owner);
		File toDelete = new File(userDir, path);

		if (toDelete.exists()) {
			if (toDelete.isFile()) {
				toDelete.delete();
			} else if (toDelete.list().length == 0) {
				toDelete.delete();
			} else {
				sr.error("Please remove the contents of the directory "
						+ "before deleting the directory itself.");
			}
		}
		return sr;

	}

}
