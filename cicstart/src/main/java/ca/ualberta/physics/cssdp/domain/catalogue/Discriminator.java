/* ============================================================
 * Discriminator.java
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
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Api(value = "An arbitratory identifier to make one Data Product "
		+ "unique from another within a Project that would otherwise be "
		+ "the same if described by Observatories and Instrument Types alone.")
@XmlRootElement
@Entity
@Table(name = "catalogue_discriminator")
public class Discriminator extends Persistent {

	private static final long serialVersionUID = 1L;

	@JsonBackReference("project-discriminators")
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@ApiModelProperty(required = true, value = "The external key for lookup of this Discriminator", dataType = "Mnemonic")
	@XmlAttribute
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	@Column(name = "ext_key", length = 50, nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.MnemonicType")
	private Mnemonic externalKey;

	@ApiModelProperty(required = false, value = "A description of this Discriminator.  Max length 1024.")
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
		this.project = ((Project) parent);
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
