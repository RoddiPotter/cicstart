/* ============================================================
 * EmailServiceImpl.java
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
package ca.ualberta.physics.cssdp.auth.service;

import org.apache.commons.mail.HtmlEmail;

import ca.ualberta.physics.cssdp.configuration.AuthServer;

import com.google.common.base.Throwables;

public class EmailServiceImpl implements EmailService {

	public EmailServiceImpl() {
	}

	public void sendEmail(String from, String to, String subject, String body) {

		try {

			String host = AuthServer.properties().getString("smtpHost");
			int port = AuthServer.properties().getInt("smtpPort");
			final String user = AuthServer.properties().getString(
					"smtpUsername");
			final String password = AuthServer.properties().getString(
					"smtpPassword");
			String systemEmail = AuthServer.properties().getString(
					"systemEmailAddress");
			boolean useSSL = AuthServer.properties().getBoolean("smtpUseSSL");
			boolean debug = AuthServer.properties().getBoolean("smtpDebug");

			HtmlEmail msg = new HtmlEmail();
			msg.setHostName(host);
			msg.setAuthentication(user, password);
			msg.setSmtpPort(port);
			msg.setSSL(useSSL);
			msg.setDebug(debug);
			msg.setSubject("Password Reset Request");
			msg.addTo(to);
			msg.setFrom(systemEmail);
			msg.setHtmlMsg(body);
			msg.send();

		} catch (Exception e) {
			e.printStackTrace();
			throw Throwables.propagate(Throwables.getRootCause(e));
		} finally {
		}

	}
}
