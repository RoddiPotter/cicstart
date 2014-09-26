package ca.ualberta.physics.cicstart.macro.resource;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.domain.macro.InstanceSpec;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class MacroResourceTests extends MacroTestsScaffolding {

	@Test
	public void testStartVM() {

		User dataManager = setupDataManager();
		String sessionToken = login(dataManager.getEmail(), "password");

		InstanceSpec instanceSpec = new InstanceSpec();
		instanceSpec.setCloud("DAIR");
		instanceSpec.setFlavor("m1.tiny");
		instanceSpec.setImage("Ubuntu 12.04.5");
		instanceSpec.setRequestId("TESTJOB");

		// String macroUrl = Common.properties().getString("api.url") +
		// "/macro";
		Response res = given().content(instanceSpec).and()
				.contentType(ContentType.JSON).and()
				.headers("CICSTART.session", sessionToken)
				.post(ResourceUrls.MACRO + "/vm");

		if (res.getStatusCode() != 200) {
			System.out.println(res.asString());
		}

		Assert.assertEquals("Expecting 200, not " + res.getStatusCode(), 200,
				res.getStatusCode());

		Instance instance = res.as(Instance.class);

		// cleanup, stop the instance
		expect().statusCode(200).when().given().content(instance).and()
				.contentType(ContentType.JSON).and()
				.headers("CICSTART.session", sessionToken)
				.delete(ResourceUrls.MACRO + "/vm");

		// TODO release floating ips
		Assert.fail(instance.id + " release my ip address!");
	}

}
