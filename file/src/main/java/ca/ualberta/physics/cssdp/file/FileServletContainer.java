/* ============================================================
 * FileServletContainer.java
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
package ca.ualberta.physics.cssdp.file;

import javax.servlet.ServletException;

import ca.ualberta.physics.cssdp.configuration.CommonServletContainer;
import ca.ualberta.physics.cssdp.file.remote.RemoteServers;

import com.google.inject.Inject;

/**
 * Binds the File Server Resources to the JerseryServlet and maps them to the
 * appropriate url mapping
 */
public class FileServletContainer extends CommonServletContainer {

	private static final long serialVersionUID = 1L;

	@Inject
	private RemoteServers remoteServers;
	private Thread remoteServersDaemon;

	@Override
	public void init() throws ServletException {
		super.init();

		InjectorHolder.inject(this);

		remoteServersDaemon = new Thread(remoteServers, "Remote Servers");
		remoteServersDaemon.setDaemon(true);
		remoteServersDaemon.start();

	}

	@Override
	public void destroy() {
		remoteServersDaemon.interrupt();
		super.destroy();
	}
}
