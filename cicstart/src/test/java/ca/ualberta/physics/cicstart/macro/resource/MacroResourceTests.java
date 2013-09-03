package ca.ualberta.physics.cicstart.macro.resource;

import static com.jayway.restassured.RestAssured.expect;

import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.Common;
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
		instanceSpec.setImage("Ubuntu 12.04 cloudimg amd64");
		instanceSpec.setRequestId("TESTJOB");

		String macroUrl = Common.properties().getString("api.url") + "/macro";
		Response res = expect().statusCode(200).when().given()
				.content(instanceSpec).and().contentType(ContentType.JSON)
				.and().headers("CICSTART.session", sessionToken)
				.post(macroUrl + "/macro.json/vm");

		Instance instance = res.as(Instance.class);

		// cleanup, stop the instance
		expect().statusCode(200).when().given().content(instance).and()
				.contentType(ContentType.JSON).and()
				.headers("CICSTART.session", sessionToken)
				.delete(macroUrl + "/macro.json/vm");

		// TODO release floating ips
		
	}

}
