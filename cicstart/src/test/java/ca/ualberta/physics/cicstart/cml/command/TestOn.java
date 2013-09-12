package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.nio.charset.Charset;

import org.junit.Test;

import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.google.common.io.Files;

public class TestOn extends IntegrationTestScaffolding {

	@Override
	protected String getComponetContext() {
		return "macro";
	}

	@Test
	public void testBootstrapScript() throws Exception {

		String script = Files.toString(
				new File(TestOn.class.getResource("/plot_maccs.cml").toURI()),
				Charset.forName("UTF-8"));

		String bootstrapped = On.bootstrapCMLScript(script, "vm", "10.0.28.4");
		
		System.out.println(bootstrapped);
		
//		Assert.assertEquals("");
		
	}

}
