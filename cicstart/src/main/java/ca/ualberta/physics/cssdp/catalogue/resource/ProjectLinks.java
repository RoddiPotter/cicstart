/* ============================================================
 * ProjectLinks.java
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

package ca.ualberta.physics.cssdp.catalogue.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ualberta.physics.cssdp.jaxb.Link;

import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectLinks {

	@XmlElement(name = "Project")
	@ApiModelProperty(value="The list of links", dataType="Link")
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
