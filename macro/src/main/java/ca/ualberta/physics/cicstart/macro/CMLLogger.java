package ca.ualberta.physics.cicstart.macro;

import static com.jayway.restassured.RestAssured.given;

import org.joda.time.LocalDateTime;
import org.slf4j.MDC;

import ca.ualberta.physics.cicstart.macro.service.MacroService;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.MacroServer;
import ca.ualberta.physics.cssdp.util.NetworkUtil;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import com.google.inject.Inject;

public class CMLLogger extends FileAppender<ILoggingEvent> {

	@Inject
	private MacroService macroService;

	public CMLLogger() {
		super();
		InjectorHolder.inject(this);
	}

	@Override
	protected void subAppend(ILoggingEvent event) {
		super.subAppend(event);

		String jobId = MDC.get("JobId");
		String logMessage = new LocalDateTime(event.getTimeStamp()).toString()
				+ " " + event.getThreadName() + " "
				+ event.getLevel().toString() + " "
				+ event.getFormattedMessage();

		if (NetworkUtil.currentlyRunningOn(MacroServer.properties().getString(
				"cicstart.server.internal"))) {
			macroService.writeToLogBuffer(jobId, logMessage);
		} else {

			String macroResource = Common.properties().getString(
					"macro.api.url");
			String writeUrl = macroResource + "/macro.json/{requestId}/log";
			given().content(logMessage).put(writeUrl, jobId);
		}

	}
}
