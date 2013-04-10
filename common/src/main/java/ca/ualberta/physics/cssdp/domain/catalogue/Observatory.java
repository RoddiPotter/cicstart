package ca.ualberta.physics.cssdp.domain.catalogue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;

import ca.ualberta.physics.cssdp.dao.Persistent;
import ca.ualberta.physics.cssdp.jaxb.MnemonicAdapter;
import ca.ualberta.physics.cssdp.jaxb.PointAdapter;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.model.Point;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;
import ca.ualberta.physics.cssdp.util.JSONPointDeserializer;
import ca.ualberta.physics.cssdp.util.JSONPointSerializer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass(value = "An Observatory object")
@XmlRootElement
@Entity
@Table(name = "catalogue_observatory")
public class Observatory extends Persistent {

	private static final long serialVersionUID = 1L;

	@JsonBackReference("project-observatories")
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@ApiProperty(required = true, value = "The external key for lookup of this Observatory", dataType = "Mnemonic")
	@XmlAttribute
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	@Column(name = "ext_key", length = 50, nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.MnemonicType")
	private Mnemonic externalKey;

	@JsonSerialize(using = JSONPointSerializer.class)
	@JsonDeserialize(using = JSONPointDeserializer.class)
	@ApiProperty(required = false, value = "The geographic location of this Observatory", dataType = "Point")
	@XmlElement
	@XmlJavaTypeAdapter(PointAdapter.class)
	@Column(name = "location", nullable = true)
	@Type(type = "ca.ualberta.physics.cssdp.dao.GeomPoint")
	private Point location;

	@ApiProperty(required = false, value = "A description of this Observatory.  Max length 1024.")
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

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
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
				.add("location", location).toString();
	}
}
