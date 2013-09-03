/* ============================================================
 * VfsListing.java
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
