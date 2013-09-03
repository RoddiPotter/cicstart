/* ============================================================
 * FtpsConnection.java
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
package ca.ualberta.physics.cssdp.file.remote.protocol;

import org.apache.commons.net.ftp.FTPSClient;

import ca.ualberta.physics.cssdp.domain.file.Host;


/**
 * Note: ftps is used internally for all URL that reside on FTP with SSL
 * servers. If you need to manually debug a connection, you would use a client
 * like "lftp" and modify the url to be ftp://blah/blah from ftps://blah/blah
 * 
 * To our own benefit, Apache net provides a simple implementation of FTP with
 * SSL, which just extends FTPClient.
 * 
 */
public class FtpsConnection extends FtpConnection {

	public FtpsConnection(Host config) {
		super(config);
		ftpClient = new FTPSClient("SSL");
	}

}
