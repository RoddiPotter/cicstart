/* ============================================================
 * VfsListingEntry.java
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
package ca.ualberta.physics.cssdp.domain.vfs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.jaxb.Link;
import ca.ualberta.physics.cssdp.jaxb.MnemonicAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "Entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class VfsListingEntry {

	@XmlAttribute
	private long size;

	@XmlAttribute
	private boolean isDir;

	@XmlElement
	private Link path;

	@JsonSerialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateTimeSerializer.class)
	@JsonDeserialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateTimeDeserializer.class)
	@XmlElement
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private LocalDateTime lastModified;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public Link getPath() {
		return path;
	}

	public void setPath(Link path) {
		this.path = path;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
}
