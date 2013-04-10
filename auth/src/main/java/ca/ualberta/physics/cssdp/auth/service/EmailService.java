package ca.ualberta.physics.cssdp.auth.service;

public interface EmailService {

	public abstract void sendEmail(String from, String to, String subject,
			String body);

}