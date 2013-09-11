package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.util.UrlParser;

import com.google.common.base.Throwables;
import com.jayway.restassured.response.Response;

public class GetVFS implements Command {

	private static final Logger jobLoger = LoggerFactory
			.getLogger("JOBLOGGER");

	private final String cicstartSession;
	private final String path;
	private File downloadedFile;

	public GetVFS(String cicstartSession, String path) {
		this.cicstartSession = cicstartSession;
		this.path = path;
	}

	@Override
	public void execute(CMLRuntime runtime) {

//		String authResource = Common.properties().getString("api.url") + "/auth";

		String whoisUrl = ResourceUrls.SESSION + "/{session}/whois";
		jobLoger.info("GetVFS: locating whois for session var at " + whoisUrl);

		User user = get(whoisUrl, cicstartSession).as(User.class);
		jobLoger.debug("GetVFS: found user " + user.getEmail()
				+ ", owner id=" + user.getId() + " with session var "
				+ cicstartSession);

//		String vfsResource = Common.properties().getString("api.url") + "/vfs";

		String readUrl = ResourceUrls.FILESYSTEM + "/" + user.getId()
				+ "/read?path={file}";
		jobLoger.info("GetVFS: read file data from " + readUrl + ", file="
				+ path);

		Response res = given().header("CICSTART.session", cicstartSession).get(
				readUrl, path);

		if (res.statusCode() == 200) {
			try {
				File file = new File(UrlParser.getLeaf(path));
				downloadedFile = Commands.streamToFile(res.asInputStream(),
						file);

				jobLoger.info("GetVFS: read success, data saved to "
						+ file.getAbsolutePath());

			} catch (Exception e) {
				jobLoger.error("GetVFS: read failure because "
						+ Throwables.getStackTraceAsString(e));
			}
		} else {
			jobLoger.error("GetVFS: read failure because " + res.asString());
		}

	}

	@Override
	public Object getResult() {
		return downloadedFile;
	}

}
