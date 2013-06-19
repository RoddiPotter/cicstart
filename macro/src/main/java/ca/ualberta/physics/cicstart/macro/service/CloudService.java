package ca.ualberta.physics.cicstart.macro.service;

import static com.jayway.restassured.RestAssured.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Flavor;
import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Identity;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.MacroServer;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;

/**
 * A service used to generically manage access to cloud resources. Abstracts the
 * cloud specific API into the methods offered by this service. Delegates to a
 * concrete implementation of a Cloud instance based on the cloud name given in
 * the method called. The configured clouds are statically set at startup.
 * 
 * State is not managed because all cloud users have access to various console
 * and dashboards to kill resources on demand. This service is simply used to
 * start/stop and create/kill resources as requested. If those resources to be
 * killed do not exist, nothing happens.
 * 
 * Assumes the user account is configured with the authorized identity required
 * for accessing the given cloud. If the user does not have access to that
 * cloud, an error is returned.
 * 
 * @author rpotter
 * 
 */
public class CloudService {

	// this is read-only once initialized at startup
	// key is cloud name: DAIR, CESWP, EC2
	private Map<String, Cloud> clouds = new HashMap<String, Cloud>();

	@SuppressWarnings("unchecked")
	public CloudService() {

		for (String cloudName : Splitter.on(",").split(
				MacroServer.properties().getString("clouds"))) {
			cloudName = cloudName.trim();

			String cloudImplClassName = MacroServer.properties().getString(
					cloudName + ".implementation");

			try {

				Class<Cloud> implClass = (Class<Cloud>) Class
						.forName(cloudImplClassName);

				Cloud cloud = implClass.newInstance();
				cloud.init(cloudName);
				clouds.put(cloudName, cloud);

			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(
						"No cloud implementation class named "
								+ cloudImplClassName
								+ ", fix the properties file.");

			} catch (Exception e) {
				Throwables.propagate(e);
			}
		}

	}

	public ServiceResponse<Instance> startInstance(String cloudName,
			Image image, Flavor flavor, String sessionToken, String jobId) {

		ServiceResponse<Instance> sr = new ServiceResponse<Instance>();

		Cloud cloud = clouds.get(cloudName);
		if (cloud != null) {

			String authResource = Common.properties().getString("auth.api.url");
			String whoisUrl = authResource + "/session.json/{session}/whois";
			User user = get(whoisUrl, sessionToken).as(User.class);

			Identity clientIdentity = cloud.authenticate(
					user.getOpenStackUsername(), user.getOpenStackPassword());

			// inject the cicstart public key into the users cloud identity
			cloud.putKey(clientIdentity, "cicstart", MacroServer.properties()
					.getString("cicstart.public.key"));

			Instance instance = null;
			try {
				instance = cloud.startInstance(clientIdentity, image, flavor,
						jobId);
			} catch (Exception e) {
				sr.setOk(false);
				sr.error(e.getMessage());
			}

			if (instance != null) {
				sr.setPayload(instance);
			}

		}
		return sr;

	}

	public ServiceResponse<Void> stopInstance(Instance instance,
			String sessionToken) {

		ServiceResponse<Void> sr = new ServiceResponse<Void>();

		Cloud cloud = clouds.get(instance.cloudName);
		if (cloud != null) {

			String authResource = Common.properties().getString("auth.api.url");
			String whoisUrl = authResource + "/session.json/{session}/whois";
			User user = get(whoisUrl, sessionToken).as(User.class);

			Identity identity = cloud.authenticate(user.getOpenStackUsername(),
					user.getOpenStackPassword());

			cloud.stopInstance(identity, instance);

		}
		return sr;

	}

	public ServiceResponse<List<Image>> getImages(String cloudName,
			String sessionToken) {

		ServiceResponse<List<Image>> sr = new ServiceResponse<List<Image>>();

		Cloud cloud = clouds.get(cloudName);
		if (cloud != null) {

			String authResource = Common.properties().getString("auth.api.url");
			String whoisUrl = authResource + "/session.json/{session}/whois";
			User user = get(whoisUrl, sessionToken).as(User.class);

			Identity identity = cloud.authenticate(user.getOpenStackUsername(),
					user.getOpenStackPassword());

			List<Image> images = cloud.getImages(identity);
			sr.setPayload(images);

		}

		return sr;
	}

}
