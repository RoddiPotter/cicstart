package ca.ualberta.physics.cicstart.cml.log;

import static com.jayway.restassured.RestAssured.given;

import org.joda.time.LocalDateTime;
import org.slf4j.MDC;

import ca.ualberta.physics.cssdp.configuration.Common;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class CMLLogger extends FileAppender<ILoggingEvent> {

	@Override
	protected void subAppend(ILoggingEvent event) {
		super.subAppend(event);
		
		String jobId = MDC.get("JobId");
		String macroResource = Common.properties().getString("macro.api.url");
		String writeUrl = macroResource + "/macro.json/{requestId}/log";

		String logMessage = new LocalDateTime(event.getTimeStamp()).toString()
				+ " " + event.getThreadName() + " "
				+ event.getLevel().toString() + " "
				+ event.getFormattedMessage();

		given().content(logMessage).put(writeUrl, jobId);

	}
	
}
