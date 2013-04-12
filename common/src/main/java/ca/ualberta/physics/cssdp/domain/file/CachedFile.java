/* ============================================================
 * CachedFile.java
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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.dao.Persistent;

@Entity
@Table(name = "cached_file")
public class CachedFile extends Persistent {

	private static final long serialVersionUID = 1L;

	// @Column(name = "external_key", length = 1024, nullable = false)
	@ElementCollection
	@CollectionTable(name = "cached_file_keys", joinColumns = { @JoinColumn(name = "cached_file_id") })
	@Column(name = "ext_key")
	private Set<String> externalKeys = new HashSet<String>();

	@Column(name = "local_path", length = 1024, nullable = false)
	private String localPath;

	@Column(name = "file_name", length = 1024, nullable = false)
	private String filename;

	@Column(name = "size", nullable = false)
	private long size;

	@Column(name = "md5", length = 32, nullable = false)
	private String md5;

	@Column(name = "file_tstamp")
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentLocalDateTime")
	private LocalDateTime fileTimestamp;

	@Column(name = "last_accessed")
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentLocalDateTime")
	private LocalDateTime lastAccessed;

	public CachedFile() {

	}

	public CachedFile(String filename, String md5, File cachedFile) {
		this.md5 = md5;
		this.localPath = cachedFile.getAbsolutePath();
		this.size = cachedFile.length();
		this.fileTimestamp = new LocalDateTime(cachedFile.lastModified());
		this.lastAccessed = new LocalDateTime(
				DateTimeZone.forID("America/Edmonton"));
		this.filename = filename;
	}

	@Override
	public String _pk() {
		return md5;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public LocalDateTime getFileTimestamp() {
		return fileTimestamp;
	}

	public void setFileTimestamp(LocalDateTime fileTimestamp) {
		this.fileTimestamp = fileTimestamp;
	}

	public void setLastAccessed(LocalDateTime lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public LocalDateTime getLastAccessed() {
		return lastAccessed;
	}

	/**
	 * Convenience method to return a file pointer to localPath
	 * 
	 * @return
	 */
	public File getFile() {
		return new File(localPath);
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getMd5() {
		return md5;
	}

	public Set<String> getExternalKeys() {
		return externalKeys;
	}

	public void setExternalKeys(Set<String> externalKeys) {
		this.externalKeys = externalKeys;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean exists() {
		return getFile().exists();
	}

}
