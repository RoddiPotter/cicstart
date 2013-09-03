/* ============================================================
 * EmailServiceStub.java
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
