package ca.ualberta.physics.cssdp.file.remote.protocol;

import org.apache.commons.net.ftp.FTPSClient;

import ca.ualberta.physics.cssdp.domain.file.Host;


/**
 * Note: ftps is used internally for all URL that reside on FTP with SSL
 * servers. If you need to manually debug a connection, you would use a client
 * like "lftp" and modify the url to be ftp://blah/blah from ftps://blah/blah
 * 
 * To our own benefit, Apache net provides a simple implementation of FTP with
 * SSL, which just extends FTPClient.
 * 
 */
public class FtpsConnection extends FtpConnection {

	public FtpsConnection(Host config) {
		super(config);
		ftpClient = new FTPSClient("SSL");
	}

}
