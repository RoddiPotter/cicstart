package ca.ualberta.physics.cssdp.vfs.dao;

import org.junit.Before;

import ca.ualberta.physics.cssdp.client.AuthClient;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;
import ca.ualberta.physics.cssdp.vfs.InjectorHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class VfsTestsScaffolding extends IntegrationTestScaffolding {

	protected static User vfsUser;

	@Inject
	protected AuthClient authClient;

	@Inject
	protected ObjectMapper mapper;

	public VfsTestsScaffolding() {
		InjectorHolder.inject(this);
	}

	@Override
	protected String getComponetContext() {
		return "";
	}

	@Before
	public void setupTestUsers() {

		User newVfsUser = new User();
		newVfsUser.setName("VFS USER");
		newVfsUser.setDeleted(false);
		newVfsUser.setEmail("vfsuser@nowhere.com");
		newVfsUser.setInstitution("institution");
		newVfsUser.setPassword("password");
		newVfsUser.setRole(Role.DATA_USER);

		vfsUser = authClient.addUser(newVfsUser);
	}

}
