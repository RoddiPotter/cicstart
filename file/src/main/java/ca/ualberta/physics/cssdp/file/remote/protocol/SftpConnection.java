package ca.ualberta.physics.cssdp.file.remote.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileMode.Type;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPFileTransfer;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;

import com.google.common.io.Files;

/**
 * Implements SFTP file transfers (directory listings, etc) using the SSHJ
 * library.
 * <p>
 * Re the SSHJ library, see http://github.com/shikhar/sshj and
 * http://groups.google.com/group/sshj
 */
public class SftpConnection extends RemoteConnection {

	public SftpConnection(Host hostEntry) {
		super(hostEntry);
		// use bouncy castle
		Security.addProvider(new BouncyCastleProvider());
	}

	private SSHClient ssh;

	private SFTPClient sftpClient;

	@Override
	public boolean connect() {

		boolean connected = true;

		try {
			ssh = new SSHClient();
			ssh.setConnectTimeout(Long.valueOf(getHostEntry().getTimeout())
					.intValue());
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.connect(getHostEntry().getHostname());

			String password = getHostEntry().getPassword();
			String username = getHostEntry().getUsername();
			if (username == null || password == null) {
				throw new ProtocolException("Invalid username/password", false);
			}
			ssh.authPassword(username, password);
			ssh.getConnection().join();
			sftpClient = ssh.newSFTPClient();

		} catch (UserAuthException e) {
			connected = false;
			throw new ProtocolException("Invalid username/password", false);
		} catch (TransportException e) {
			connected = false;
			throw new ProtocolException("Network error", true, e);
		} catch (IOException e) {
			connected = false;
			throw new ProtocolException("Unable to connect", true, e);
		} catch (InterruptedException e) {
			// connected!
		}
		return connected;
	}

	@Override
	public boolean disconnect() {

		try {
			ssh.disconnect();
			return true;
		} catch (IOException e) {
			// not much we can do
		}
		return false;
	}

	@Override
	public boolean isConnected() {

		if (ssh == null) {
			return false;
		}
		return ssh.isConnected() && ssh.isAuthenticated();
	}

	@Override
	public List<RemoteFile> ls(String path) {

		List<RemoteFile> list = new ArrayList<RemoteFile>();
		try {
			List<RemoteResourceInfo> remoteInfos = sftpClient.ls(path);
			for (RemoteResourceInfo info : remoteInfos) {

				String name = info.getName();
				boolean isDir = info.isDirectory();
				long size = info.getAttributes().getSize();

				if(info.getAttributes().getType().equals(Type.SYMKLINK)) {
					System.out.println("GAAAAAAAAAH - symlink bad, ug.");
					continue;
				}
				/*
				 * getMtime() (part of SSHJ library) returns seconds, but we
				 * need milliseconds
				 */
				LocalDateTime modifiedTstamp = new LocalDateTime(info
						.getAttributes().getMtime() * 1000L);

				RemoteFile remoteFile = new RemoteFile("sftp://"
						+ getHostEntry().getHostname() + path + "/" + name,
						size, modifiedTstamp, isDir);

				list.add(remoteFile);

			}
		} catch (IOException e) {
			throw new ProtocolException("Could not get listing at " + path,
					true, e);
		}
		return list;
	}

	@Override
	public InputStream download(String source) throws IOException {
		SFTPFileTransfer fileTransfer;
		fileTransfer = sftpClient.getFileTransfer();
		if (source.startsWith("sftp://")) {
			source = source.replaceAll("sftp://", "");
			source = source.replaceAll(getHostEntry().getHostname(), "");
		}

		File tempDir = Files.createTempDir();
		int filenameIndex = source.lastIndexOf("/");
		String filename = source.substring(filenameIndex);
		File downloadFile = new File(tempDir, filename);

		try {
			fileTransfer.download(source, downloadFile.getAbsolutePath());
		} catch (IOException e) {
			throw new ProtocolException("Could not download file", false, e);
		}
		return new FileInputStream(downloadFile);
	}

	// @Override
	// public void transfer(Url source, Url destination) {
	//
	// SFTPFileTransfer fileTransfer;
	// try {
	//
	// fileTransfer = sftpClient.getFileTansfer();
	// fileTransfer.setTransferListener(new TransferListener() {
	//
	// @Override
	// public void reportProgress(long arg0) {
	// // arg0 is the total bytes transfered. The contraction with Progress
	// monitor is to give the bytes
	// // transfered this update.
	// long bytesThisUpdate = arg0 - monitor.getTotalBytesRecieved();
	// monitor.onStatusUpdate((int) bytesThisUpdate);
	// }
	//
	// @Override
	// public void startedFile(String arg0, long arg1) {
	//
	// }
	//
	// @Override
	// public void startedDir(String arg0) {
	//
	// }
	//
	// @Override
	// public void finishedFile() {
	//
	// }
	//
	// @Override
	// public void finishedDir() {
	//
	// }
	// });
	// fileTransfer.download(source.getPath(), destination.getPath());
	//
	// } catch (IOException e) {
	// throw new ProtocolException("Transfer error", CAUSE.unknown, true, e);
	// }
	// }
}
