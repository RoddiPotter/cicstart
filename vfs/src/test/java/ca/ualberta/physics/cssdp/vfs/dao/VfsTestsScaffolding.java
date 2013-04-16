/* ============================================================
 * VfsTestsScaffolding.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
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
		return "/vfs";
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
