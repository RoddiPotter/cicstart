package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;
import ca.ualberta.physics.cssdp.vfs.configuration.VfsServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class TestPutVFS extends IntegrationTestScaffolding {

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	private String sessionToken;

	@Override
	protected String getComponetContext() {
		return "unsure";
	}

	public TestPutVFS() {
		InjectorHolder.inject(this);
	}

	@Test
	public void testPutVFS() throws Exception {

		User dataManager = setupDataManager();
		sessionToken = login(dataManager.getEmail(), "password");

		PutVFS putVfs = new PutVFS(sessionToken, "/testputvfs", "build.gradle");
		putVfs.execute(new CMLRuntime("testJob", sessionToken));

		String vfsRoot = VfsServer.properties().getString("vfs_root");
		File uploadedFile = new File(new File(vfsRoot, dataManager.getId().toString() + "/testputvfs"), "build.gradle");
		Assert.assertEquals("apply plugin: 'eclipse'", Files.readFirstLine(uploadedFile, Charset.forName("UTF-8")));
		
		uploadedFile.delete();
	}

}
