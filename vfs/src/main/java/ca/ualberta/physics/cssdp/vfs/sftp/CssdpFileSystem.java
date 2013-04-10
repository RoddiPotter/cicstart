package ca.ualberta.physics.cssdp.vfs.sftp;

import java.io.File;

import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;

import ca.ualberta.physics.cssdp.vfs.VfsServer;

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
