package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class TestGetVFS extends IntegrationTestScaffolding {

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	private String sessionToken;

	@Override
	protected String getComponetContext() {
		return "unsure";
	}

	public TestGetVFS() {
		InjectorHolder.inject(this);
	}

	@Test
	public void testGetVFS() throws Exception {

		User dataManager = setupDataManager();
		sessionToken = login(dataManager.getEmail(), "password");

		GetVFS getVfs = new GetVFS(sessionToken, "maccs.test");
		getVfs.execute(new CMLRuntime("testJob", sessionToken));
		File downloadedFile = (File) getVfs.getResult();

		Assert.assertEquals("maccs.test", downloadedFile.getName());
		Assert.assertEquals(
				"#gnuplot -e \"filename='PGG20120108_l0_sec.sec'\" maccs.gp",
				Files.readFirstLine(downloadedFile, Charset.forName("UTF-8")));
		downloadedFile.delete();
	}

}
