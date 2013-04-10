package ca.ualberta.physics.cssdp.auth.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import ca.ualberta.physics.cssdp.auth.dao.SessionDao;
import ca.ualberta.physics.cssdp.auth.dao.UserDao;
import ca.ualberta.physics.cssdp.domain.auth.Session;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.service.ManualTransaction;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.service.Transactional;
import ca.ualberta.physics.cssdp.util.HashUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

public class UserService {

	private static final int NUM_HASHES_TO_COMPUTE = 1000;

	private Cache<User, String> ipWhiteList = CacheBuilder.newBuilder()
			.expireAfterWrite(48, TimeUnit.HOURS).build();

	@Inject
	private UserDao userDao;

	@Inject
	private SessionDao sessionDao;

	@Inject
	private EntityManager em;

	@Transactional
	public ServiceResponse<Void> create(User user) {

		ServiceResponse<Void> sr = new ServiceResponse<Void>();

		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			sr.error("Unable to complete request:" + e.getMessage());
		}

		if (sr.isRequestOk()) {
			User exists = userDao.find(user.getEmail());

			if (exists == null || exists.isDeleted()) {

				updatePasswordDigestAndSalt(user, random);
				if (exists == null) {
					userDao.save(user);
				} else {
					exists.replaceWith(user);
					userDao.update(exists);
				}

				sr.info("Email successfully registered.");
			} else {

				// wait a bit to fool the intruder
				byte[] bSalt = new byte[8];
				random.nextBytes(bSalt);
				// Digest computation
				HashUtils.getHash(NUM_HASHES_TO_COMPUTE, user.getPassword(),
						bSalt);

				sr.warn("Email already registered");
			}
		}

		return sr;
	}

	/*
	 * DRY method to remove duplicate code between register and reset password
	 */
	private void updatePasswordDigestAndSalt(User user, SecureRandom random) {

		// Salt generation 64 bits long
		byte[] bSalt = new byte[8];
		random.nextBytes(bSalt);
		// Digest computation
		byte[] bDigest = HashUtils.getHash(NUM_HASHES_TO_COMPUTE,
				user.getPassword(), bSalt);
		String sDigest = HashUtils.byteToBase64(bDigest);
		String sSalt = HashUtils.byteToBase64(bSalt);

		user.setPasswordDigest(sDigest);
		user.setPasswordSalt(sSalt);
	}

	public ServiceResponse<User> find(String email) {

		return new ServiceResponse<User>(userDao.find(email));

	}

	/**
	 * The users password must compute to the same hashed value as what is
	 * stored locally. If it does, their ip address is cached for 48 hours
	 * allowing them to use the system without having to login again for this
	 * time if they continue to use the same ip.
	 * 
	 * @param email
	 * @param password
	 * @param userIpAddress
	 * @return
	 */
	public ServiceResponse<Session> authenticate(String email, String password,
			String requestIp) {

		ServiceResponse<Session> sr = new ServiceResponse<Session>();

		User user = userDao.find(email);

		String digest = null;
		String salt = null;

		if (user != null) {
			digest = user.getPasswordDigest();
			salt = user.getPasswordSalt();
			// DATABASE VALIDATION
			if (digest == null || salt == null) {
				throw new RuntimeException(
						"Database inconsistant Salt or Digested Password altered");
			}

		} else {
			// TIME RESISTANT ATTACK (Even if the user does not exist the
			// Computation time is equal to the time needed for a legitimate
			// user
			digest = "000000000000000000000000000=";
			salt = "00000000000=";

		}

		byte[] bDigest = HashUtils.base64ToByte(digest);
		byte[] bSalt = HashUtils.base64ToByte(salt);

		// Compute the new DIGEST
		byte[] proposedDigest = HashUtils.getHash(NUM_HASHES_TO_COMPUTE,
				password, bSalt);

		if (Arrays.equals(proposedDigest, bDigest) && user != null
				&& !user.isDeleted()) {

			ipWhiteList.invalidate(user);
			ipWhiteList.put(user, requestIp);

			final Session session = new Session(user, UUID.randomUUID()
					.toString().split("=")[0]);

			new ManualTransaction(sr, em) {

				@Override
				public void doInTransaction() {
					sessionDao.save(session);
				}

				@Override
				public void onError(Exception e, ServiceResponse<?> sr) {
					sr.error(e.getMessage());
				}
			};

			sr.setPayload(session);

		} else {
			sr.error("Invalid login credentials");
		}

		return sr;
	}

	public ServiceResponse<Session> locate(String sessionToken) {
		Session session = sessionDao.find(sessionToken);
		return new ServiceResponse<Session>(session);
	}

	// public void requestPasswordReset(String email, String sourceIp) {
	//
	// User user = userDao.find(email);
	// if (user != null && !user.isDeleted()) {
	//
	// // generate the token and save it in the cache
	// SecureRandom random;
	// try {
	// random = SecureRandom.getInstance("SHA1PRNG");
	// } catch (NoSuchAlgorithmException e) {
	// throw Throwables.propagate(e);
	// }
	// // Salt generation 64 bits long
	// byte[] bToken = new byte[8];
	// random.nextBytes(bToken);
	// String sToken = HashUtils.byteToBase64(bToken);
	//
	// resetTokens.put(email, sToken);
	//
	// Configuration cfg = new Configuration();
	// cfg.setClassForTemplateLoading(getClass(), "");
	//
	// cfg.setObjectWrapper(new DefaultObjectWrapper());
	//
	// try {
	// Template temp = cfg.getTemplate("ResetPassword.ftl");
	//
	// Map<String, String> root = new HashMap<String, String>();
	// root.put("baseUrl",
	// Server.properties().getString("base_webapp_url"));
	// root.put("resetPath",
	// Server.properties().getString("password_reset_path"));
	// root.put("resetToken", "/" + sToken);
	// root.put("sourceIp", sourceIp);
	// root.put("email", email);
	// root.put("website",
	// Server.properties().getString("base_webapp_url"));
	// root.put("supportPath",
	// Server.properties().getString("support_path"));
	//
	// Writer out = new StringWriter();
	// temp.process(root, out);
	// out.flush();
	//
	// // send it
	// emailService.sendEmail(
	// Server.properties().getString("system_email"), email,
	// "Password Reset Request", out.toString());
	//
	// } catch (Exception e) {
	// throw Throwables.propagate(e);
	// }
	//
	// }
	//
	// }
	//
	// @Transactional
	// public ServiceResponse<Void> resetPassword(String email, String password,
	// String resetToken) {
	//
	// ServiceResponse<Void> sr = new ServiceResponse<Void>();
	// // ensure the token is valid
	// String token = resetTokens.getIfPresent(email);
	// if (token == null) {
	// sr.error("Reset token has expired, please obtain a new one");
	// return sr;
	// } else {
	// if (!token.equals(resetToken)) {
	// sr.error("Reset token does not match, please obtain a new one");
	// // prevent someone from trying over and over.
	// resetTokens.invalidate(email);
	// return sr;
	// }
	// }
	//
	// // remove the token so it can't be used again by anyone else.
	// resetTokens.invalidate(email);
	//
	// // lookup the user
	// User user = userDao.find(email);
	// if (user == null || user.isDeleted()) {
	// sr.error("User account not found");
	// return sr;
	// }
	//
	// // update the password digest
	// SecureRandom random;
	// try {
	// random = SecureRandom.getInstance("SHA1PRNG");
	// } catch (NoSuchAlgorithmException e) {
	// sr.error("Unable to complete request:" + e.getMessage());
	// return sr;
	// }
	//
	// user.setPassword(password);
	//
	// updatePasswordDigestAndSalt(user, random);
	//
	// // save the user
	// userDao.update(user);
	//
	// sr.info("Password updated");
	//
	// return sr;
	// }

}
