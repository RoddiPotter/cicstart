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
