package ca.ualberta.physics.cssdp.domain.catalogue;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ualberta.physics.cssdp.dao.Persistent;

import com.google.common.base.Throwables;
import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass(value="The configuration used to match files visited during a server scan.")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "catalogue_metadataparserconfig")
public class MetadataParserConfig extends Persistent {

	private static final long serialVersionUID = 1L;

	@ApiProperty(required=true, value="The regular expression used for inclusion; map these files to the Data Product associated with this MetadataParserConfig.")
	@XmlElement
	@Column(name = "includes_regex", length = 1024, nullable = false)
	private String includesRegex;

	@ApiProperty(required=false, value="The regular expression used for parsing the start date from file names.")
	@XmlElement
	@Column(name = "start_date_regex", length = 1024, nullable = true)
	private String startDateRegex;

	@ApiProperty(required=false, value="The regular expression used for parsing the end date from file names.")
	@XmlElement
	@Column(name = "end_date_regex", length = 1024, nullable = true)
	private String endDateRegex;

	@ApiProperty(required=false, value="The bean shell expression used for parsing the start date from file names.")
	@XmlElement
	@Column(name = "start_date_bean_shell", nullable = true)
	private String startDateBeanShell;

	@ApiProperty(required=false, value="The bean shell expression used for parsing the end date from file names.")
	@XmlElement
	@Column(name = "end_date_bean_shell", nullable = true)
	private String endDateBeanShell;

	@Override
	public String _pk() {
		StringBuffer buffy = new StringBuffer();
		for (Field field : getClass().getFields()) {
			try {
				buffy.append(field.get(this).toString());
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}
		return buffy.toString();
	}

	public String getIncludesRegex() {
		return includesRegex;
	}

	public void setIncludesRegex(String includesRegex) {
		this.includesRegex = includesRegex;
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

	public void setStartDateRegex(String startDateRegex) {
		this.startDateRegex = startDateRegex;
	}

	public String getStartDateRegex() {
		return startDateRegex;
	}

	public void setEndDateRegex(String endDateRegex) {
		this.endDateRegex = endDateRegex;
	}

	public String getEndDateRegex() {
		return endDateRegex;
	}

}
