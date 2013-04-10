package ca.ualberta.physics.cssdp.domain.catalogue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;

import ca.ualberta.physics.cssdp.dao.Persistent;
import ca.ualberta.physics.cssdp.jaxb.MnemonicAdapter;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass(value = "Describes an Instrument Type")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "catalogue_instrumenttype")
public class InstrumentType extends Persistent {

	private static final long serialVersionUID = 1L;

	@JsonBackReference("project-instrumentTypes")
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@ApiProperty(required = true, value = "The external key for lookup of this Instrument Type", dataType = "Mnemonic")
	@XmlAttribute
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	@Column(name = "ext_key", length = 50, nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.MnemonicType")
	private Mnemonic externalKey;

	@ApiProperty(required = false, value = "The description of this Instrument Type", dataType = "String")
	@XmlElement
	@Column(name = "description", length = 1024, nullable = true)
	private String description;

	@Override
	public String _pk() {
		return project._pk() + externalKey._pk();
	}

	/**
	 * This is a callback method used by JAXB to set the Site object properly
	 * after unmarshaling the xml/json data back into Java. Without the @XmlTransient
	 * on the site field above and this method, we either see "cyclic reference"
	 * errors, or the site field remains null.
	 * 
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		this.project = (Project) parent;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Mnemonic getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(Mnemonic externalKey) {
		this.externalKey = externalKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("key", externalKey)
				.add("project", project != null ? project.getId() : null)
				.toString();
	}
}
