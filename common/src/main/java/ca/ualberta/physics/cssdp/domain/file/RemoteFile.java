/* ============================================================
 * RemoteFile.java
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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.jaxb.LocalDateTimeAdapter;
import ca.ualberta.physics.cssdp.util.JSONLocalDateTimeDeserializer;
import ca.ualberta.physics.cssdp.util.JSONLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private String url;

	@XmlAttribute
	private long size;

	@XmlAttribute
	private boolean isDir;

	@JsonSerialize(using=JSONLocalDateTimeSerializer.class)
	@JsonDeserialize(using=JSONLocalDateTimeDeserializer.class)
	@XmlAttribute
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime modifedTstamp;

	public RemoteFile() {

	}

	public RemoteFile(String url, long size, LocalDateTime modifiedTstamp,
			boolean isDir) {
		this.url = url;
		this.size = size;
		this.modifedTstamp = modifiedTstamp;
		this.isDir = isDir;
	}

	public long getSize() {
		return size;
	}

	public boolean isDir() {
		return isDir;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		RemoteFile other = (RemoteFile) obj;
		return url.equals(other.url);
	}

	public LocalDateTime getModifedTstamp() {
		return modifedTstamp;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public void setModifedTstamp(LocalDateTime modifedTstamp) {
		this.modifedTstamp = modifedTstamp;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

}
