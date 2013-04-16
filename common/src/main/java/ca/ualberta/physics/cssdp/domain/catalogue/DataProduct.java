/* ============================================================
 * DataProduct.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;

import ca.ualberta.physics.cssdp.dao.Persistent;
import ca.ualberta.physics.cssdp.jaxb.MnemonicAdapter;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@ApiClass(value = "Describes a set of data files that should be logically grouped together.")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "catalogue_dataproduct")
public class DataProduct extends Persistent {

	private static final long serialVersionUID = 1L;

	@JsonBackReference("project-dataProducts")
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@ApiProperty(required = true, value = "The external key for lookup of this Data Product", dataType = "Mnemonic")
	@XmlAttribute
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	@Column(name = "ext_key", length = 50, nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.MnemonicType")
	private Mnemonic externalKey;

	@ApiProperty(required = false, value = "A description of this Data Product.  Max length 1024.")
	@XmlElement
	@Column(name = "description", length = 1024, nullable = true)
	private String description;

	@JsonIgnore
	@XmlTransient
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "catalogue_dataproduct_observatory", joinColumns = @JoinColumn(name = "dataproduct_id"), inverseJoinColumns = @JoinColumn(name = "observatory_id"))
	private List<Observatory> observatories = new ArrayList<Observatory>();

	@JsonInclude(Include.NON_EMPTY)
	@Transient
	@ApiProperty(required = false, value = "A list of Observatory keys that are related to this Data Product", dataType = "Mnemonic")
	@XmlElementWrapper(name = "observatories")
	@XmlElement(name = "observatory")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private List<Mnemonic> observatoryKeys = new ArrayList<Mnemonic>();

	@JsonIgnore
	@XmlTransient
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "catalogue_dataproduct_instrumenttype", joinColumns = @JoinColumn(name = "dataproduct_id"), inverseJoinColumns = @JoinColumn(name = "instrumenttype_id"))
	private List<InstrumentType> instrumentTypes = new ArrayList<InstrumentType>();

	@JsonInclude(Include.NON_EMPTY)
	@Transient
	@ApiProperty(required = false, value = "A list of Instrument Type keys that are related to this Data Product", dataType = "Mnemonic")
	@XmlElementWrapper(name = "instrumentTypes")
	@XmlElement(name = "instrumentType")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private List<Mnemonic> instrumentTypeKeys = new ArrayList<Mnemonic>();

	@XmlElement
	@ApiProperty(required = false, value = "The configuration used to map Data Files to this Data Product during a directory scan of the server", dataType = "MetadataParserConfig")
	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "metadataparserconfig_id")
	private MetadataParserConfig metadataParserConfig;

	@JsonIgnore
	@XmlTransient
	@ManyToOne
	@JoinTable(name = "catalogue_dataproduct_discriminator", joinColumns = { @JoinColumn(name = "dataproduct_id") }, inverseJoinColumns = { @JoinColumn(name = "discriminator_id") })
	private Discriminator discriminator;

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@JsonInclude(Include.NON_NULL)
	@Transient
	@ApiProperty(required = false, value = "A Discriminator key that helps to make this Data Product unique within a project", dataType = "Mnemonic")
	@XmlElement(name = "discriminator")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private Mnemonic discriminatorKey;

	@Transient
	private transient Pattern includesPattern = null;

	@Transient
	private transient Pattern excludesPattern = null;

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
		afterJsonUmarshal(project);
	}

	public void afterJsonUmarshal(Project project) {

		for (Mnemonic key : observatoryKeys) {
			observatories.add(project.getObservatory(key));
		}

		for (Mnemonic key : instrumentTypeKeys) {
			instrumentTypes.add(project.getInstrumentType(key));
		}

		if (discriminatorKey != null) {
			discriminator = project.getDiscriminator(discriminatorKey);
		}

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

	public boolean addObservatory(Observatory o) {
		observatoryKeys.add(o.getExternalKey());
		return observatories.add(o);
	}

	public void addObservatories(List<Observatory> observatories) {
		for (Observatory o : observatories) {
			addObservatory(o);
		}
	}

	public List<Observatory> getObservatories() {
		return ImmutableList.copyOf(observatories);
	}

	public void setObservatories(List<Observatory> observatories) {
		addObservatories(observatories);
	}

	public List<InstrumentType> getInstrumentTypes() {
		return ImmutableList.copyOf(instrumentTypes);
	}

	public boolean addInstrumentType(InstrumentType it) {
		instrumentTypeKeys.add(it.getExternalKey());
		return instrumentTypes.add(it);
	}

	public void addInstrumentTypes(List<InstrumentType> its) {
		for (InstrumentType it : its) {
			addInstrumentType(it);
		}
	}

	public void setInstrumentTypes(List<InstrumentType> instrumentTypes) {
		addInstrumentTypes(instrumentTypes);
	}

	public MetadataParserConfig getMetadataParserConfig() {
		return metadataParserConfig;
	}

	public void setMetadataParserConfig(
			MetadataParserConfig metadataParserConfig) {
		this.metadataParserConfig = metadataParserConfig;
	}

	public void setDiscriminator(Discriminator discriminator) {
		this.discriminator = discriminator;
		if (discriminator != null) {
			this.discriminatorKey = discriminator.getExternalKey();
		}
	}

	public Discriminator getDiscriminator() {
		return discriminator;
	}

	/**
	 * Tests whether this url should be excluded from mapping to this data
	 * product or not.
	 * 
	 * @param url
	 * @return
	 */
	public boolean shouldExclude(String url) {
		String excludesRegex = getProject().getExcludesRegex();
		if (!Strings.isNullOrEmpty(excludesRegex)) {
			if (excludesPattern == null) {
				excludesPattern = Pattern.compile(excludesRegex);
			}
			Matcher excludesMatcher = excludesPattern.matcher(url);

			if (excludesMatcher.find()) {
				return true;
			} else {
				return false;
			}
		} else {
			// no excludes set, assume false
			return false;
		}
	}

	/**
	 * Tests whether this url should be included in the mapping to this data
	 * product or not.
	 * 
	 * @param url
	 * @return
	 */
	public boolean shouldInclude(String url) {
		String includesRegex = metadataParserConfig.getIncludesRegex();

		if (includesPattern == null) {
			includesPattern = Pattern.compile(includesRegex);
		}
		Matcher includesMatcher = includesPattern.matcher(url);

		if (includesMatcher.find()) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String toString() {
		return Objects
				.toStringHelper(this)
				.add("key", externalKey)
				.add("project", project != null ? project.getId() : null)
				.add("observatories", Joiner.on(", ").join(observatoryKeys))
				.add("instrumentTypes",
						Joiner.on(", ").join(instrumentTypeKeys))
				.add("discriminator", discriminatorKey).toString();
	}

}
