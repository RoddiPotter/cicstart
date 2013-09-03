/* ============================================================
 * Host.java
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
package ca.ualberta.physics.cssdp.domain.file;

import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ualberta.physics.cssdp.dao.Persistent;

import com.google.common.base.Objects;
import com.wordnik.swagger.annotations.ApiProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@Entity
@Table(name = "host_entry")
public class Host extends Persistent {

	public static enum Protocol {
		file, ftp, sftp, ftps
	}

	private static final long serialVersionUID = 1L;

	@Column(name = "hostname", length = 100, nullable = false)
	private String hostname;

	@Column(name = "protocol", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private Protocol protocol;

	@Column(name = "username", length = 100, nullable = false)
	private String username;

	@Column(name = "password", length = 100, nullable = false)
	private String password;

	@Column(name = "max_connections", nullable = false)
	private int maxConnections = 1;

	@ApiProperty(value="Milliseconds before tiemout")
	@Column(name = "timeout", nullable = false)
	private int timeout = (int) TimeUnit.MILLISECONDS.convert(1,
			TimeUnit.MINUTES);

	@ApiProperty(value="Milliseconds to wait")
	@Column(name = "retry_wait", nullable = false)
	private int retryWait = (int) TimeUnit.MILLISECONDS.convert(1,
			TimeUnit.MINUTES);

	@Column(name = "retry_count", nullable = false)
	private int retryCount = 3;

	public Host() {

	}

	public Host(Protocol protocol, String hostname, String username,
			String password) {
		this(protocol, hostname, username, password, 1, 60000, 60000, 3);
	}

	public Host(Protocol protocol, String hostname, String username,
			String password, int maxConnections, int timeout, int retryWait,
			int retryCount) {

		this.protocol = protocol;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.maxConnections = maxConnections;
		this.timeout = timeout;
		this.retryWait = retryWait;
		this.retryCount = retryCount;

	}

	public Host(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	@Override
	public String _pk() {
		return hostname;
	}

	@XmlElement(name = "hostname")
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@XmlElement(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name = "maxConnections")
	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@XmlElement(name = "timeout")
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@XmlElement(name = "retryWait")
	public long getRetryWait() {
		return retryWait;
	}

	public void setRetryWait(int retryWait) {
		this.retryWait = retryWait;
	}

	@XmlElement(name = "retryCount")
	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@XmlElement(name = "protocol")
	@ApiProperty(allowableValues = "file, ftp, sftp, ftps")
	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("protocol", protocol)
				.add("hostname", hostname).add("username", username).toString();
	}

	public void maskPassword() {
		setPassword("******");
	}

	public void maskUser() {
		setUsername("******");
	}

}
