package ca.ualberta.physics.cicstart.cml.log;

import static com.jayway.restassured.RestAssured.given;

import org.joda.time.LocalDateTime;
import org.slf4j.MDC;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cicstart.macro.service.MacroService;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.util.NetworkUtil;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import com.google.inject.Inject;

public class CMLLogger extends FileAppender<ILoggingEvent> {

	private Object latch = new Object();

	@Inject
	private MacroService macroService;

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

			// lazy access to macro service because
			// avoid NPE due to logging framework initializing before guice is
			// done initializing
			synchronized (latch) {
				if (macroService == null) {
					InjectorHolder.inject(this);
				}
			}
			macroService.writeToLogBuffer(jobId, logMessage + "\n");

		} else {

//			String macroResource = Common.properties().getString("api.url")
//					+ "/macro";
			String writeUrl = ResourceUrls.MACRO + "/{requestId}/log";
			given().content(logMessage).put(writeUrl, jobId);
		}

	}
}
