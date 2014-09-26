package ca.ualberta.physics.cicstart.macro.resource;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cicstart.cml.command.CMLRuntime;
import ca.ualberta.physics.cicstart.cml.command.Run;
import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;
import ca.ualberta.physics.cssdp.util.FileUtil;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class TestBinary extends IntegrationTestScaffolding {

	protected static User dataManagerBinary;

	@Inject
	private AuthClient authClient;

	public TestBinary() {
		InjectorHolder.inject(this);
	}

	@Override
	protected String getComponetContext() {
		return "/macro";
	}

	@Before
	public void setupTestUsers() {

		User newDataManager = new User();
		newDataManager.setName("Data Manager Binary");
		newDataManager.setDeleted(false);
		newDataManager.setEmail("datamanagerbinary@nowhere.com");
		newDataManager.setInstitution("institution");
		newDataManager.setPassword("password");
		newDataManager.setRole(Role.DATA_MANAGER);

		dataManagerBinary = authClient.addUser(newDataManager);
	}

	@Test
	public void testBuildBinaryClient() throws Exception {

		// you may need to regenerate the binary client by running

		// macro/gradle distZip
		// cd build/distributions
		// unzip macro-1.0.zip

		File macro = new File(TestBinary.class.getResource("/test.cml").toURI());
		byte[] script = Files.toByteArray(macro);
		final Response res = given()
				.content(script)
				.and()
				.contentType(ContentType.BINARY)
				.and()
				.header("CICSTART.session",
						login(dataManagerBinary.getEmail(), "password")).expect()
				.statusCode(200).when().post(ResourceUrls.MACRO + "/bin");

		File tempDir = Files.createTempDir();
		String contentDisposition = res.getHeader("Content-Disposition");
		Pattern pattern = Pattern.compile("((?<=\").*(?=\"))");
		Matcher matcher = pattern.matcher(contentDisposition);
		matcher.find();
		String filename = matcher.group();
		File binaryMarco = new File(tempDir, filename);
		Files.copy(new InputSupplier<InputStream>() {

			@Override
			public InputStream getInput() throws IOException {
				return res.asInputStream();
			}

		}, binaryMarco);

		// unzip it
		File tarFile = FileUtil.unGzip(binaryMarco, tempDir);
		FileUtil.unTar(tarFile, tempDir);

		Run run = new Run("./run");
		run.setWorkingDirectory(new File(tarFile.getParentFile(), "bin"));
		run.execute(new CMLRuntime("test", "test"));

		// should have a normal exit code
		Assert.assertEquals(new Integer(0), (Integer) run.getResult());

		// cleanup
		for (File file : FileUtil.walk(tempDir)) {
			file.delete();
		}
		tarFile.delete();
		binaryMarco.delete();

	}

}
