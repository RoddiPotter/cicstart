/* ============================================================
 * CatalogueSearchResponse.java
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
