package ca.ualberta.physics.cssdp.vfs.ftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

public class VfsFtplet extends DefaultFtplet {

	
	@Override
	public FtpletResult onConnect(FtpSession session) throws FtpException,
			IOException {
		
		InetSocketAddress socketAddr = session.getClientAddress();
		InetAddress addr = socketAddr.getAddress();
		String ipAddress = addr.getHostAddress();
		String email = session.getUserArgument();
		
		System.out.println("it was: " + email + "=" + ipAddress);
		
		
		return FtpletResult.DEFAULT;
	}
	@Override
	public FtpletResult onLogin(FtpSession session, FtpRequest request)
			throws FtpException, IOException {

		InetSocketAddress socketAddr = session.getClientAddress();
		InetAddress addr = socketAddr.getAddress();
		String ipAddress = addr.getHostAddress();
		String email = session.getUserArgument();
		
		System.out.println("checking whitelist for: " + email + "=" + ipAddress);
		
		return FtpletResult.DEFAULT;
	}
	
}
