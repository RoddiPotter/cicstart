package ca.ualberta.physics.cssdp.auth.service;


public class EmailServiceStub implements EmailService {

	public static String theToken;
	
	public EmailServiceStub() {
	}

	public void sendEmail(String from, String to, String subject, String body) {

		System.out.println("*****************************************************");
		System.out.println("Email begin");
		System.out.println("From:" + from);
		System.out.println("To:" + to);
		System.out.println("Subject:" + subject);
		System.out.println("-----------------------------");
		System.out.println("Body:\n" + body);
		System.out.println("*****************************************************");
		System.out.println("See ThreadLoad for the message body");
		
		int startIndex = body.indexOf("password_reset/") + "password_reset/".length();
		String token = body.substring(startIndex, body.indexOf("\">", startIndex));
		theToken = token;
		
	}

}
