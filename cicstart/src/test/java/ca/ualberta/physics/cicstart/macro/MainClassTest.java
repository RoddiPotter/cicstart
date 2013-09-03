package ca.ualberta.physics.cicstart.macro;

import java.io.File;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.io.Files;

public class MainClassTest {

	@Test
	public void testFinishesProperly() throws Exception {

		MainClass main = new MainClass();

		String script = Files.toString(new File(MainClassTest.class
				.getResource("/test.cml").toURI()), Charset.forName("UTF-8"));

		ServiceResponse<String> sr = main.runCmlScript(script, "token", "UNIT_TEST");

		// if it hits this, then it's good. Hanging is no good.
		Assert.assertTrue(sr.isRequestOk());

	}
}
