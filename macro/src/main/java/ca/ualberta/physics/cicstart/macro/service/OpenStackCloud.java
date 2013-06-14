package ca.ualberta.physics.cicstart.macro.service;

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.macro.InjectorHolder;
import ca.ualberta.physics.cssdp.client.AuthClient;
import ca.ualberta.physics.cssdp.configuration.MacroServer;
import ca.ualberta.physics.cssdp.domain.macro.Instance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.net.InetAddresses;
import com.google.inject.Inject;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class OpenStackCloud implements Cloud {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStackCloud.class);

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	private String cloudName;

	private String osAuthUrl = "http://208.75.74.10:5000/v2.0";

	private String imageRef = "http://208.75.74.10:8774/v2/{tenantId}/images";
	private String flavorRef = "http://208.75.74.10:8774/v2/{tenantId}/flavors";
	private String serversRef = "http://208.75.74.10:8774/v2/{tenantId}/servers";

	public void init(String cloudName) {
		this.cloudName = cloudName;

		Properties props = MacroServer.properties().getSet(cloudName);
		osAuthUrl = props.getProperty(cloudName + ".authUrl",
				"http://208.75.74.10:5000/v2.0");
		String novaUrl = props.getProperty(cloudName + ".nova",
				"http://208.75.74.10:8774/v2");
		imageRef = novaUrl + "/{tenantId}/images";
		flavorRef = novaUrl + "/{tenantId}/flavors";
		serversRef = novaUrl + "/{tenantId}/servers";
	}

	public static enum Flavor {

		m1_tiny(1, 1, 512), m1_small(10, 1, 2048), m1_medium(12, 2, 2048), m1_large(
				13, 4, 8192), m1_xlarge(11, 8, 16384);

		public Integer flavorId;
		public Integer cpus;
		public Integer ram;

		Flavor(Integer id, Integer cpus, Integer ram) {
			this.flavorId = id;
			this.cpus = cpus;
			this.ram = ram;
		}

	}

	/*
	 * A wrapper object to hold some open stack authentication data. These have
	 * to be public for FasterXML to locate them
	 */
	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	public static class Identity {

		public Auth auth = new Auth();

		public static class Auth {

			public PasswordCredentials passwordCredentials = new PasswordCredentials();
			public String tenantId;
			public String token;

			public static class PasswordCredentials {
				public String username;
				public String password;
			}

		}

	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	public static class CreateServer {

		public Server server = new Server();

		public static class Server {
			public String flavorRef;
			public String imageRef;
			public String name;
			// public String key_name = "cicstart"; // for testing purposes
		}

	}

	public OpenStackCloud() {
		InjectorHolder.inject(this);
	}

	@Override
	public Instance startInstance(Identity identity, Image image,
			Flavor flavor, String name) {

		// start the instance
		CreateServer createServer = new CreateServer();
		createServer.server.flavorRef = flavorRef.replaceAll("\\{tenantId\\}",
				identity.auth.tenantId) + "/" + flavor.flavorId;
		createServer.server.imageRef = image.href;

		createServer.server.name = name;

		String createServerUrl = serversRef.replaceAll("\\{tenantId\\}",
				identity.auth.tenantId);
		String jsonServer = serialize(createServer);

		Response res = given().header("X-Auth-Token", identity.auth.token)
				.and().header("Content-Type", "application/json").and()
				.content(jsonServer).post(createServerUrl);

		// avoid infinite loop below in server status check
		if (res.getStatusCode() == 202) {

			// get some reference info so we can interact with it
			Instance instance = new Instance();
			instance.cloudName = cloudName;

			String createServerResponseJson = res.asString();
			logger.debug("create server json response : "
					+ createServerResponseJson);
			JsonPath createServerResponseJsonPath = JsonPath
					.from(createServerResponseJson);
			instance.password = createServerResponseJsonPath
					.getString("server.adminPass");
			instance.href = res.getHeader("location");

			// get more detailed info and wait until instance is ready to use
			res = given().header("X-Auth-Token", identity.auth.token).get(
					instance.href);

			String instanceQueryResponseJson = res.asString();
			logger.debug("instance json response : "
					+ instanceQueryResponseJson);
			JsonPath instanceQueryResponseJsonPath = JsonPath
					.from(instanceQueryResponseJson);
			String serverStatus = instanceQueryResponseJsonPath
					.getString("server.status");

			while (!serverStatus.equals("ACTIVE")) {

				res = given().header("X-Auth-Token", identity.auth.token).get(
						instance.href);

				instanceQueryResponseJson = res.asString();
				instanceQueryResponseJsonPath = JsonPath
						.from(instanceQueryResponseJson);
				serverStatus = instanceQueryResponseJsonPath
						.getString("server.status");
				try {
					logger.debug("Server not ready, status is " + serverStatus);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					// TODO repeat until instance created and then delete
					// automatically

				}
			}

			// final bit of info, the internal ip address we need to log into
			// the machine
			instance.ipAddress = instanceQueryResponseJsonPath
					.getString("server.addresses.novanetwork_28[0].addr");

			try {
				InetAddresses.forString(instance.ipAddress).isReachable(1);
			} catch (IOException e) {

				// TODO allocate and assign external ip
				
				/*
				 * Note that DAIR does not expose the Quantum service. This
				 * means that I can't download a CML binary client and run the
				 * script in the cloud, unless I'm logged into the machine
				 * already.
				 */

			}

			return instance;

		} else {
			throw new IllegalStateException("Could not start instance because "
					+ (res.getStatusCode() == 413 ? "over limit"
							: "open stack error " + res.getStatusCode()));
		}

	}

	public Identity authenticate(String osUser, String osPassword) {

		// find the tenant id
		Identity identity = new Identity();
		identity.auth.passwordCredentials.username = osUser;
		identity.auth.passwordCredentials.password = osPassword;

		String jsonIdentity = serialize(identity);

		// get a temporary auth token to lookup tenant
		Response res = given().content(jsonIdentity).and()
				.header("Content-Type", "application/json")
				.post(osAuthUrl + "/tokens");

		String tempToken = JsonPath.from(res.asString()).getString(
				"access.token.id");
		res = given().header("X-Auth-Token", tempToken).get(
				osAuthUrl + "/tenants");

		// grab the tenant id
		identity.auth.tenantId = JsonPath.from(res.asString()).get(
				"tenants[0].id");

		jsonIdentity = serialize(identity);

		// get an auth token using the identity loaded with the tenant id
		res = given().content(jsonIdentity).and()
				.header("Content-Type", "application/json")
				.post(osAuthUrl + "/tokens");

		identity.auth.token = JsonPath.from(res.asString()).getString(
				"access.token.id");

		return identity;

	}

	private String serialize(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			Throwables.propagate(e);
		}
		return null;
	}

	@Override
	public Object createVolume() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String attachVolume() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopInstance(Identity identity, Instance instance) {

		given().header("X-Auth-Token", identity.auth.token).delete(
				instance.href);
		// TODO handle errors
	}

	@Override
	public void detachVolume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteVolume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSnapshot() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Image> getImages(Identity identity) {

		String imagesUrl = imageRef.replaceAll("\\{tenantId\\}",
				identity.auth.tenantId);

		Response res = given().header("X-Auth-Token", identity.auth.token).get(
				imagesUrl);

		List<Image> images = new ArrayList<Image>();
		String sResponse = res.asString();
		JsonPath jsonPath = JsonPath.from(sResponse);

		List<Map<String, Object>> imagesList = jsonPath.getList("images");
		for (Map<String, Object> data : imagesList) {

			Image image = new Image();
			image.href = (String) ((List<Map<String, Object>>) data
					.get("links")).get(0).get("href");
			image.id = (String) data.get("id");
			image.name = (String) data.get("name");

			images.add(image);
		}
		return images;
	}

}
