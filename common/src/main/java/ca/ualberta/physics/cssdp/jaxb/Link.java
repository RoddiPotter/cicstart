package ca.ualberta.physics.cssdp.jaxb;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {

	@XmlAttribute(name = "rel")
	protected String relationship;
	
	@XmlAttribute
	protected String href;

	public Link() {
	}

	public Link(String relationship, URI href) {
		this(relationship, href.toASCIIString());
	}

	public Link(String relationship, String href) {
		this.relationship = relationship;
		this.href = href;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
