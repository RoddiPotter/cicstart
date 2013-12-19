package ca.ualberta.physics.cicstart.macro.service;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.util.NetworkUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class OpenStackCloud implements Cloud {

	public static final Logger logger = LoggerFactory
			.getLogger(OpenStackCloud.class);

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	private String cloudName;

	private String osAuthUrl = null;

	private String imagesUrl = null;
	private String flavorRef = null;
	private String serversRef = null;
	private String keypairsRef = null;
	private String ipRef = null;

	public void init(String cloudName) {
		this.cloudName = cloudName;

		Properties props = MacroServer.properties().getSet(cloudName);
		osAuthUrl = props.getProperty(cloudName + ".identity");
		imagesUrl = props.getProperty(cloudName + ".image");
		String computeUrl = props.getProperty(cloudName + ".compute");
		flavorRef = computeUrl + "/{tenantId}/flavors";
		serversRef = computeUrl + "/{tenantId}/servers";
		keypairsRef = computeUrl + "/{tenantId}/os-keypairs";
		ipRef = computeUrl + "/{tenantId}/os-floating-ips";
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
	public static class Identity {

		public Auth auth = new Auth();

		public static class Auth {

			public PasswordCredentials passwordCredentials = new PasswordCredentials();

			private String tenantId;
			private String token;

			public static class PasswordCredentials {
				public String username;
				public String password;
			}

			@JsonInclude(Include.NON_NULL)
			public String getTenantId() {
				return tenantId;
			}

			public void setTenantId(String tenantId) {
				this.tenantId = tenantId;
			}

			@JsonInclude(Include.NON_NULL)
			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
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
			// TODO make this a property
			public String key_name = "cicstart";
		}

	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	public static class KeyPairRequest {

		public KeyPair keypair = new KeyPair();

		public static class KeyPair {
			public String name;
			public String public_key;
		}

	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	public static class FloatingIpRequest {

		public AddFloatingIp addFloatingIp = new AddFloatingIp();

		public static class AddFloatingIp {
			public String address;
		}

	}

	public OpenStackCloud() {
		InjectorHolder.inject(this);
	}

	@Override
	public Instance startInstance(Identity identity, Image image,
			Flavor flavor, String ref) {

		LocalDateTime start = LocalDateTime.now();

		// start the instance
		CreateServer createServer = new CreateServer();
		createServer.server.flavorRef = flavorRef.replaceAll("\\{tenantId\\}",
				identity.auth.getTenantId()) + "/" + flavor.flavorId;
		createServer.server.imageRef = image.href;

		createServer.server.name = ref;

		String jsonServer = serialize(createServer);

		Response res = given().header("X-Auth-Token", identity.auth.getToken())
				.and().header("Content-Type", "application/json").and()
				.content(jsonServer)
				.post(serversRef, identity.auth.getTenantId());

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
			instance.id = createServerResponseJsonPath.getString("server.id");
			instance.href = res.getHeader("location");

			// get more detailed info and wait until instance is ready to use
			res = given().header("X-Auth-Token", identity.auth.getToken()).get(
					instance.href);

			String instanceQueryResponseJson = res.asString();
			logger.debug("instance json response : "
					+ instanceQueryResponseJson);
			JsonPath instanceQueryResponseJsonPath = JsonPath
					.from(instanceQueryResponseJson);
			String serverStatus = instanceQueryResponseJsonPath
					.getString("server.status");

			while (!serverStatus.equals("ACTIVE")) {

				res = given().header("X-Auth-Token", identity.auth.getToken())
						.get(instance.href);

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
				}
			}

			// handle interruption from while loop nicely
			if (Thread.currentThread().isInterrupted()) {
				stopInstance(identity, instance);
				return instance;
			}

			String internalIp = instanceQueryResponseJsonPath
					.getString("server.addresses.novanetwork_28[0].addr");

			String cicstartInternalIp = MacroServer.properties().getString(
					"cicstart.server.internal");

			String cicstartExternalIp = MacroServer.properties().getString(
					"cicstart.server.external");

			if (NetworkUtil.isReachable(cicstartInternalIp, 1)) {

				// use internal because we can access CICSTART on internal IP

				// networking seems to take a minute.. let it finish
				// to avoid connection refused & no route to host
				// errors
				boolean reachable = NetworkUtil.isReachable(internalIp, 10);
				if (reachable) {
					logger.info(internalIp + " is reachable: " + reachable);
					instance.ipAddress = internalIp;
				} else {

					logger.error(instance.ipAddress
							+ " could not be reached, someone is wrong");
					throw new RuntimeException(
							"Could not associated external ip to server "
									+ instance.id + " giving up.");

				}

			} else if (NetworkUtil.isReachable(cicstartExternalIp, 1)) {

				logger.info("Allocating and assigned external IP address because CICSTART server is external");

				res = given().header("X-Auth-Token", identity.auth.getToken())
						.post(ipRef, identity.auth.getTenantId());

				if (res.getStatusCode() == 200) {

					JsonPath ipPath = JsonPath.from(res.asString());
					String externalIp = ipPath.getString("floating_ip.ip");

					FloatingIpRequest ipRequest = new FloatingIpRequest();
					ipRequest.addFloatingIp.address = externalIp;

					res = given()
							.header("X-Auth-Token", identity.auth.getToken())
							.and()
							.content(ipRequest)
							.and()
							.contentType(ContentType.JSON)
							.post(serversRef + "/{server_id}/action",
									identity.auth.getTenantId(), instance.id);

					if (res.getStatusCode() == 202) {
						// networking seems to take a minute.. let it finish
						// to avoid connection refused & no route to host
						// errors

						if (NetworkUtil.isReachable(externalIp, 10)) {
							logger.info(externalIp
									+ " is now reachable, have fun!");
							instance.ipAddress = externalIp;
						} else {
							logger.error(externalIp
									+ " could not be reached, someone is wrong");
							throw new RuntimeException(
									"Could not associated external ip to server "
											+ instance.id + " giving up.");
						}

					} else if (res.getStatusCode() != 200) {
						throw new IllegalStateException(
								"Could not associated external ip to server "
										+ instance.id + " open stack error "
										+ res.getStatusCode() + "\n"
										+ res.asString());
					}

				} else {
					throw new IllegalStateException(
							"Could not allocate external ip because "
									+ (res.getStatusCode() == 413 ? "over limit"
											: "open stack error "
													+ res.getStatusCode())
									+ "\n" + res.asString());
				}

			} else {
				stopInstance(identity, instance);
				throw new IllegalStateException(
						"Can't access CICSTART server on internal network at "
								+ cicstartInternalIp
								+ " or external ips, this makes spawned VM useless");
			}

			LocalDateTime end = LocalDateTime.now();

			logger.info("Instance " + instance.ipAddress + "(" + ref
					+ ") took "
					+ Seconds.secondsBetween(start, end).getSeconds()
					+ " seconds to become accessible.");

			return instance;

		} else {
			throw new IllegalStateException("Could not start instance because "
					+ (res.getStatusCode() == 413 ? "over limit"
							: "open stack error " + res.getStatusCode()) + "\n"
					+ res.asString());
		}

	}

	public Identity authenticate(String osUser, String osPassword) {

		// find the tenant id
		Identity identity = new Identity();
		identity.auth.passwordCredentials.username = osUser;
		identity.auth.passwordCredentials.password = osPassword;

		String jsonIdentity = serialize(identity);

		System.out.println(jsonIdentity);

		// get a temporary auth token to lookup tenant
		Response res = given().content(jsonIdentity).and()
				.header("Content-Type", "application/json")
				.post(osAuthUrl + "/tokens");

		// System.out.println(res.asString());

		String tempToken = JsonPath.from(res.asString()).getString(
				"access.token.id");
		res = given().header("X-Auth-Token", tempToken).get(
				osAuthUrl + "/tenants");

		// grab the tenant id
		identity.auth.setTenantId(JsonPath.from(res.asString())
				.get("tenants[0].id").toString());

		jsonIdentity = serialize(identity);
		System.out.println("use this to get real auth token " + jsonIdentity);

		// get an auth token using the identity loaded with the tenant id
		res = given().content(jsonIdentity).and()
				.header("Content-Type", "application/json")
				.post(osAuthUrl + "/tokens");

		identity.auth.setToken(JsonPath.from(res.asString()).getString(
				"access.token.id"));

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

		given().header("X-Auth-Token", identity.auth.getToken()).delete(
				instance.href);

		// TODO release IP address

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

	@Override
	public List<Image> getImages(Identity identity) {

		RequestSpecification request = given().header("X-Auth-Token",
				identity.auth.getToken());
		request.log().all(true);
		Response res = request.get(imagesUrl);

		// curl -v -H "X-Auth-Token:9beed78b9219498a9b5fe0ded9b96416"
		// http://208.75.74.10:9292/v2/images

		List<Image> images = new ArrayList<Image>();
		String sResponse = res.asString();
		// System.out.println(sResponse);
		JsonPath jsonPath = JsonPath.from(sResponse);

		List<Map<String, Object>> imagesList = jsonPath.getList("images");
		for (Map<String, Object> data : imagesList) {

			Image image = new Image();
			image.href = imagesUrl
					+ data.get("self").toString().replaceAll("/v2/images", "");
			image.id = (String) data.get("id");
			image.name = (String) data.get("name");

			images.add(image);
		}
		return images;
	}

	@Override
	public void putKey(Identity clientIdentity, String keyname, String publicKey) {

		KeyPairRequest kpRequest = new KeyPairRequest();
		kpRequest.keypair.name = keyname;
		kpRequest.keypair.public_key = publicKey;

		given().header("X-Auth-Token", clientIdentity.auth.getToken()).and()
				.header("Content-Type", "application/json").and()
				.content(kpRequest)
				.post(keypairsRef, clientIdentity.auth.getTenantId());

	}

}
