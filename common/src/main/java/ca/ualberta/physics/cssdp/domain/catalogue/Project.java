package ca.ualberta.physics.cssdp.domain.catalogue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Type;

import ca.ualberta.physics.cssdp.dao.Persistent;
import ca.ualberta.physics.cssdp.jaxb.MnemonicAdapter;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass(value = "A Project object describes Data Products which are mapped to Data Files "
		+ "located on a Host.  Mapping is accomplished through scanning the directories of the "
		+ "host and using the metadata configured on the Project and Data Product.")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "catalogue_project")
public class Project extends Persistent {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@ApiProperty(required = true, value = "Must be unique system wide.", dataType = "Mnemonic")
	@XmlAttribute
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	@Column(name = "ext_key", length = 50, nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.MnemonicType")
	private Mnemonic externalKey;

	@ApiProperty(required = true, value = "Max length is 1024 chars")
	@XmlElement
	@Column(name = "name", length = 1024, nullable = false)
	private String name;

	@ApiProperty(required = false, value = "Max length is 1024 chars")
	@XmlElement
	@Column(name = "url", length = 1024, nullable = true)
	private String url;

	@ApiProperty(required = false, value = "Points to the \"Rules of the road\" document.  Max "
			+ "length is 1024 chars")
	@XmlElement
	@Column(name = "rules_url", length = 1024, nullable = true)
	private String rulesUrl;

	@JsonManagedReference("project-observatories")
	@ApiProperty(required = false, value = "The list of observatories used in data products", dataType = "Observatory")
	@XmlElementWrapper(name = "observatories")
	@XmlElement(name = "observatory")
	@OneToMany(mappedBy = "project", cascade = { CascadeType.ALL })
	private List<Observatory> observatories = new ArrayList<Observatory>();

	@JsonManagedReference("project-instrumentTypes")
	@ApiProperty(required = false, value = "The list of instrument types used in data products", dataType = "InstrumentType")
	@XmlElementWrapper(name = "instrumentTypes")
	@XmlElement(name = "instrumentType")
	@OneToMany(mappedBy = "project", cascade = { CascadeType.ALL })
	private List<InstrumentType> instrumentTypes = new ArrayList<InstrumentType>();

	@JsonManagedReference("project-discriminators")
	@ApiProperty(required = false, value = "The list of arbitrary descriminators used in data products", dataType = "Discriminator")
	@XmlElementWrapper(name = "discriminators")
	@XmlElement(name = "discriminator")
	@OneToMany(mappedBy = "project", cascade = { CascadeType.ALL })
	private List<Discriminator> discriminators = new ArrayList<Discriminator>();

	@JsonManagedReference("project-dataProducts")
	@ApiProperty(required = false, value = "The list of data products describing the files found on the host", dataType = "DataProduct")
	@XmlElementWrapper(name = "dataProducts")
	@XmlElement(name = "dataProduct")
	@OneToMany(mappedBy = "project", cascade = { CascadeType.ALL })
	private List<DataProduct> dataProducts = new ArrayList<DataProduct>();

	@ApiProperty(required = false, value = "Regular expression that parses the start date out of "
			+ "file names on the server.  Follow http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html "
			+ "for regex rules.")
	@XmlElement
	@Column(name = "start_date_regex", length = 1024, nullable = true)
	private String startDateRegex;

	@ApiProperty(required = false, value = "Regular expression that parses the end date out of file "
			+ "names on the server.  Follow http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html "
			+ "for regex rules.")
	@XmlElement
	@Column(name = "end_date_regex", length = 1024, nullable = true)
	private String endDateRegex;

	@ApiProperty(required = false, value = "The regular expression used to include files while scanning "
			+ "the host. Follow http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html for regex "
			+ "rules.")
	@XmlElement
	@Column(name = "excludes_regex", length = 1024, nullable = true)
	private String excludesRegex;

	@ApiProperty(required = false, value = "Beanshell that parses the start date out of file "
			+ "names on the server.")
	@XmlElement
	@Column(name = "start_date_bean_shell", nullable = true)
	private String startDateBeanShell;

	@ApiProperty(required = false, value = "Beanshell that parses or calculates the start date out "
			+ "of file names on the server.")
	@XmlElement
	@Column(name = "end_date_bean_shell", nullable = true)
	private String endDateBeanShell;

	@ApiProperty(required = false, value = "The host that stores the project data files.")
	@XmlElement
	@Column(name = "host", nullable = true)
	private String host;

	@ApiProperty(required = false, value = "The list of directories to start scanning at")
	@XmlElementWrapper(name = "scanDirectories")
	@XmlElement(name = "directory")
	@ElementCollection
	@CollectionTable(name = "catalogue_scandirectories", joinColumns = @JoinColumn(name = "project_id"))
	@Column(name = "directory", nullable = false)
	private List<String> scanDirectories = new ArrayList<String>();

	@Override
	public String _pk() {
		return externalKey._pk();
	}

	public Mnemonic getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(Mnemonic externalKey) {
		this.externalKey = externalKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRulesUrl() {
		return rulesUrl;
	}

	public void setRulesUrl(String rulesUrl) {
		this.rulesUrl = rulesUrl;
	}

	public List<DataProduct> getDataProducts() {
		return dataProducts;
	}

	public void setDataProducts(List<DataProduct> dataProducts) {
		this.dataProducts = dataProducts;
	}

	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = observatories;
	}

	public List<InstrumentType> getInstrumentTypes() {
		return instrumentTypes;
	}

	public void setInstrumentTypes(List<InstrumentType> instrumentTypes) {
		this.instrumentTypes = instrumentTypes;
	}

	public List<Discriminator> getDiscriminators() {
		return discriminators;
	}

	public void setDiscriminators(List<Discriminator> discriminators) {
		this.discriminators = discriminators;
	}

	public Observatory getObservatory(Mnemonic key) {

		for (Observatory o : observatories) {
			if (o.getExternalKey().equals(key)) {
				return o;
			}
		}

		throw new IllegalStateException("No observatory defined with key "
				+ key);
	}

	public InstrumentType getInstrumentType(Mnemonic key) {

		for (InstrumentType it : instrumentTypes) {
			if (it.getExternalKey().equals(key)) {
				return it;
			}
		}

		throw new IllegalStateException("No instrument type defined with key "
				+ key);
	}

	public Discriminator getDiscriminator(Mnemonic key) {

		for (Discriminator d : discriminators) {
			if (d.getExternalKey().equals(key)) {
				return d;
			}
		}

		throw new IllegalStateException("No discriminator defined with key "
				+ key);
	}

	public String getStartDateBeanShell() {
		return startDateBeanShell;
	}

	public void setStartDateBeanShell(String startDateBeanShell) {
		this.startDateBeanShell = startDateBeanShell;
	}

	public String getEndDateBeanShell() {
		return endDateBeanShell;
	}

	public void setEndDateBeanShell(String endDateBeanShell) {
		this.endDateBeanShell = endDateBeanShell;
	}

	public String getStartDateRegex() {
		return startDateRegex;
	}

	public void setStartDateRegex(String startDateRegex) {
		this.startDateRegex = startDateRegex;
	}

	public String getEndDateRegex() {
		return endDateRegex;
	}

	public void setEndDateRegex(String endDateRegex) {
		this.endDateRegex = endDateRegex;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<String> getScanDirectories() {
		return scanDirectories;
	}

	public void setScanDirectories(List<String> scanDirectories) {
		this.scanDirectories = scanDirectories;
	}

	public String getExcludesRegex() {
		return excludesRegex;
	}

	public void setExcludesRegex(String excludesRegex) {
		this.excludesRegex = excludesRegex;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("key", externalKey)
				.add("observatories", Joiner.on(", ").join(observatories))
				.add("instrumentTypes", Joiner.on(", ").join(instrumentTypes))
				.add("dataProducts", Joiner.on(", ").join(dataProducts))
				.toString();
	}
}
