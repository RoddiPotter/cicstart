package ca.ualberta.physics.cssdp.vfs.ftp;

import java.util.List;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

import ca.ualberta.physics.cssdp.client.AuthClient;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.vfs.InjectorHolder;
import ca.ualberta.physics.cssdp.vfs.VfsServer;

import com.google.inject.Inject;

public class VfsFtpUserManager implements UserManager {

	@Inject
	private AuthClient authClient;

	public VfsFtpUserManager() {
		InjectorHolder.inject(this);
	}

	@Override
	public User authenticate(Authentication arg0)
			throws AuthenticationFailedException {

		User user = null;

		if (arg0.getClass().equals(UsernamePasswordAuthentication.class)) {
			UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) arg0;

			final String email = auth.getUsername();
			final String password = auth.getPassword();

			// delegated login
			ServiceResponse<String> sr = authClient.login(email, password);
			if (sr.isRequestOk()) {

				ca.ualberta.physics.cssdp.domain.auth.User cicstartUser = authClient
						.whois(sr.getPayload());

				// use the CICSTART user id in case the user changes their email
				// address
				final Long fUserId = cicstartUser.getId();

				user = new User() {
					@Override
					public String getHomeDirectory() {
						return VfsServer.properties().getString("vfs_root")
								+ "/" + fUserId.toString();
					}

					@Override
					public AuthorizationRequest authorize(
							AuthorizationRequest arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<Authority> getAuthorities() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<Authority> getAuthorities(
							Class<? extends Authority> arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean getEnabled() {
						return true;
					}

					@Override
					public int getMaxIdleTime() {
						return 10000;
					}

					@Override
					public String getName() {
						return email;
					}

					@Override
					public String getPassword() {
						return "password";
					}
				};
			} else {
				throw new AuthenticationFailedException("Please login at "
						+ Common.properties().getString("auth.url"));
			}

		}
		return user;
	}

	@Override
	public void delete(String arg0) throws FtpException {
		// unused
	}

	@Override
	public boolean doesExist(String arg0) throws FtpException {
		// unused
		return false;
	}

	@Override
	public String getAdminName() throws FtpException {
		// unused
		return null;
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		// unused
		return null;
	}

	@Override
	public User getUserByName(String arg0) throws FtpException {
		// unused
		return null;
	}

	@Override
	public boolean isAdmin(String arg0) throws FtpException {
		// unused
		return false;
	}

	@Override
	public void save(User arg0) throws FtpException {
		// unused
	}

}
