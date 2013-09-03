/* ============================================================
 * CssdpFileSystem.java
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

import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;

import ca.ualberta.physics.cssdp.vfs.configuration.VfsServer;

public class CssdpFileSystem implements FileSystemView {

	private final File root;

	public CssdpFileSystem(String username) {
		File vfsRoot = new File(VfsServer.properties().getString("vfs_root"));
		root = new File(vfsRoot, username);
	}

	@Override
	public SshFile getFile(String file) {
		return new CssdpSshFile(root, file);
	}

	@Override
	public SshFile getFile(SshFile baseDir, String file) {
		return new CssdpSshFile(root, file);
	}

}
