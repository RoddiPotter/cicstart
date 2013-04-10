package ca.ualberta.physics.cssdp.domain.vfs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ualberta.physics.cssdp.jaxb.Link;

@XmlRootElement(name = "Listing")
@XmlAccessorType(XmlAccessType.FIELD)
public class VfsListing {

	@XmlElement
	private Link path;

	@XmlElementWrapper
	@XmlElement
	private List<VfsListingEntry> entires = new ArrayList<VfsListingEntry>();
	
	public VfsListing() {

	}

	public Link getPath() {
		return path;
	}

	public void setPath(Link path) {
		this.path = path;
	}

	public List<VfsListingEntry> getEntires() {
		return entires;
	}

	public void setEntires(List<VfsListingEntry> entires) {
		this.entires = entires;
	}


}
