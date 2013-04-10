package ca.ualberta.physics.cssdp.file.resource;

import org.junit.Before;

import ca.ualberta.physics.cssdp.client.AuthClient;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;
import ca.ualberta.physics.cssdp.file.InjectorHolder;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class FileTestsScaffolding extends IntegrationTestScaffolding {

	protected static User dataManager;

	@Inject
	private AuthClient authClient;

	@Inject
	protected ObjectMapper mapper;
	
	public FileTestsScaffolding() {
		InjectorHolder.inject(this);
	}

	@Override
	protected String getComponetContext() {
		return "";
	}

	@Before
	public void setupTestUsers() {

		User newDataManager = new User();
		newDataManager.setName("Data Manager");
		newDataManager.setDeleted(false);
		newDataManager.setEmail("datamanager@nowhere.com");
		newDataManager.setInstitution("institution");
		newDataManager.setPassword("password");
		newDataManager.setRole(Role.DATA_MANAGER);

		dataManager = authClient.addUser(newDataManager);
	}

}
