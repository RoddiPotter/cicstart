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
