package ca.ualberta.physics.cssdp.domain.catalogue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogueSearchResponse {

	@XmlElementWrapper(name = "urls")
	@XmlElement(name = "url")
	private List<URI> uris = new ArrayList<URI>();

	public CatalogueSearchResponse() {
	}

	public void setUris(List<URI> uris) {
		this.uris = uris;
	}

	public List<URI> getUris() {
		return uris;
	}

	@Override
	public String toString() {
		ToStringHelper toStringHelper = Objects.toStringHelper(this);
		for (URI uri : uris) {
			toStringHelper.addValue('\n').add("uri", uri.toASCIIString());
		}
		return toStringHelper.toString();
	}
}
