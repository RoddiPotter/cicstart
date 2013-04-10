package ca.ualberta.physics.cssdp.catalogue.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiProperty;

import ca.ualberta.physics.cssdp.jaxb.Link;

@XmlRootElement(name = "Projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectLinks {

	@XmlElement(name = "Project")
	@ApiProperty(value="The list of links", dataType="Link")
	private List<Link> projectLinks = new ArrayList<Link>();

	public void addLink(Link link) {
		projectLinks.add(link);
	}
	
	public void setProjectLinks(List<Link> projectLinks) {
		this.projectLinks = projectLinks;
	}


	public List<Link> getProjectLinks() {
		return projectLinks;
	}
	
}
