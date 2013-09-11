package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;

import com.jayway.restassured.response.Response;

public class PutVFS implements Command {

	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	private final String cicstartSession;
	private final String dir;
	private String file;

	public PutVFS(String cicstartSession, String dir, String file) {
		this.cicstartSession = cicstartSession;
		this.dir = dir;
		this.file = file;
	}

	@Override
	public void execute(CMLRuntime runtime) {

//		String authResource = Common.properties().getString("api.url") + "/auth";

		String whoisUrl = ResourceUrls.SESSION + "/{session}/whois";
		jobLogger.debug("PutVFS: locating whois for session var at " + whoisUrl);
		User user = get(whoisUrl, cicstartSession).as(User.class);
		jobLogger.debug("PutVFS: found user " + user.getEmail()
				+ ", owner id=" + user.getId() + " with session var "
				+ cicstartSession);

//		String vfsResource = Common.properties().getString("api.url") + "/vfs";

		File fileToUpload = new File(file);

		String writeUrl = ResourceUrls.FILESYSTEM + "/{owner}/write";
		jobLogger.info("PutVFS: writing file " + file + " to " + writeUrl + ", owner="
				+ user.getId());

		Response res = given().header("CICSTART.session", cicstartSession)
				.and().multiPart("file", fileToUpload).and()
				.formParam("path", dir).post(writeUrl, user.getId());

		if (res.statusCode() != 201) {
			jobLogger.error("PutVFS: write failed because " + res.asString());
		}

	}

	@Override
	public Object getResult() {
		return null;
	}

	public String getCicstartSession() {
		return cicstartSession;
	}

	public String getDir() {
		return dir;
	}

	public String getFile() {
		return file;
	}

}
