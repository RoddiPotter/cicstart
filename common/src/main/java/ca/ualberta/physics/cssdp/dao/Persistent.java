package ca.ualberta.physics.cssdp.dao;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.wordnik.swagger.annotations.ApiProperty;

import ca.ualberta.physics.cssdp.model.Model;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Persistent extends Model {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	@ApiProperty(value = "Omit for adding new objects. Required for udpates.")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@XmlAttribute
	@ApiProperty(value = "Omit for adding new objects. Required for udpates.")
	@Version
	private int version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
