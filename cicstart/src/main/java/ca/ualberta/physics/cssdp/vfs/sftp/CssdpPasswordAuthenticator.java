/* ============================================================
 * CssdpPasswordAuthenticator.java
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

import static com.jayway.restassured.RestAssured.given;

import java.net.SocketAddress;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;

import com.jayway.restassured.response.Response;

public class CssdpPasswordAuthenticator implements PasswordAuthenticator {

	private static final Logger logger = LoggerFactory
			.getLogger(CssdpPasswordAuthenticator.class);


	public CssdpPasswordAuthenticator() {
	}

	@Override
	public boolean authenticate(String username, String password,
			ServerSession session) {

		SocketAddress remoteAddress = session.getIoSession().getRemoteAddress();
		String remoteIpAddress = remoteAddress.toString();

		Response res = given().formParam("email", username)
				.formParam("password", password)
				.formParam("ip", remoteIpAddress).when()
				.post(ResourceUrls.SESSION);

		if (res.getStatusCode() == 200) {
			logger.debug(username
					+ " authenticated successfully with Auth resource");
			return true;
		} else {
			logger.warn("Auth resource returned errors: "
					+ res.getHeader("environet-auth-errors"));
		}

		return false;
	}

}
