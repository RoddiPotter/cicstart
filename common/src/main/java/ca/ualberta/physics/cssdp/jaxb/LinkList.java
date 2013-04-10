package ca.ualberta.physics.cssdp.jaxb;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "links")
public class LinkList extends AbstractList<Link> {

	@XmlElementRef
	private List<Link> links = new ArrayList<Link>();

	@Override
	public boolean add(Link e) {
		return links.add(e);
	}
	
	@Override
	public Link get(int arg0) {
		return links.get(arg0);
	}

	@Override
	public int size() {
		return links.size();
	}

}
