package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.given;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.MacroServer;
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.domain.macro.InstanceSpec;
import ca.ualberta.physics.cssdp.util.NetworkUtil;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class StartVM implements Command {

	private static final Logger logger = LoggerFactory.getLogger(StartVM.class);
	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	private final String cicstartSession;
	private final String jobId;
	private final String cloudName;
	private final String imageName;
	private final String flavor;

	// the started instance
	private Instance instance;

	public StartVM(String cicstartSession, String jobId, String cloudName,
			String imageName, String flavor) {
		this.cicstartSession = cicstartSession;
		this.jobId = jobId;
		this.cloudName = cloudName;
		this.imageName = imageName;
		this.flavor = flavor;
	}

	@Override
	public void execute(CMLRuntime runtime) {

		String cicstartServer = MacroServer.properties().getString(
				"cicstart.server.internal");

		if (NetworkUtil.currentlyRunningOn(cicstartServer)) {

			InstanceSpec vmSpec = new InstanceSpec();
			vmSpec.setCloud(cloudName);
			vmSpec.setFlavor(flavor);
			vmSpec.setImage(imageName);
			vmSpec.setRequestId(jobId);

			logger.info("StartVM: Starting VM instance on " + cloudName
					+ " using image " + imageName + " of size " + flavor);
			String macroUrl = Common.properties().getString("macro.api.url");
			Response res = given().content(vmSpec).and()
					.contentType(ContentType.JSON).and()
					.headers("CICSTART.session", cicstartSession)
					.post(macroUrl + "/macro.json/vm");

			if (res.statusCode() == 200) {
				instance = res.as(Instance.class);
				logger.info("StartVM: Instance started with ip address "
						+ instance.ipAddress + " and id " + instance.id);
			}

		} else {

			jobLogger
					.info("StartVM: not starting another VM from a CML spawned VM, "
							+ "they are started only from a CICSTART server.");
		}

	}

	@Override
	public Object getResult() {
		return instance;
	}

}
