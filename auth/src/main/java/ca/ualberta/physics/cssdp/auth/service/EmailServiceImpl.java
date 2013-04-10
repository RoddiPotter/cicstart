package ca.ualberta.physics.cssdp.auth.service;

import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.mail.HtmlEmail;

import ca.ualberta.physics.cssdp.configuration.Common;

import com.google.common.base.Throwables;

public class EmailServiceImpl implements EmailService {

	public EmailServiceImpl() {
	}

	public void sendEmail(String from, String to, String subject, String body) {

		try {

			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
			Session mailSession = Session.getDefaultInstance(props, null);

			Transport transport;
			try {
				transport = mailSession.getTransport("smtp");
			} catch (NoSuchProviderException e) {
				throw Throwables.propagate(e);
			}

			transport.connect(Common.properties()
					.getString("email_server_host"), Common.properties()
					.getInt("email_smtp_port"),
					Common.properties().getString("email_server_username"),
					Common.properties().getString("email_server_password"));

			HtmlEmail htmlEmail = new HtmlEmail();
			htmlEmail.setMailSession(mailSession);
			htmlEmail.setSubject("Password Reset Request");
			htmlEmail.addTo(to);
			htmlEmail.setFrom(Common.properties().getString("system_email"));
			htmlEmail.setHtmlMsg(body);
			htmlEmail.send();

		} catch (Exception e) {
			throw Throwables.propagate(Throwables.getRootCause(e));
		}

	}

}
