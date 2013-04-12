/* ============================================================
 * RemoteServers.java
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
package ca.ualberta.physics.cssdp.file.remote;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.remote.command.CommandRequest;
import ca.ualberta.physics.cssdp.file.remote.command.RemoteServerCommand;

public interface RemoteServers extends Runnable {

	public int requestOperation(RemoteServerCommand<?> command);
	public CommandRequest getRequest(Integer requestId);
	public void remove(Host he);
	public boolean contains(Host hostEntry);
	public void add(Host hostEntry);
	
}