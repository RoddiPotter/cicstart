/* ============================================================
 * CatalogueSearchRequest.java
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.jaxb.LocalDateTimeAdapter;
import ca.ualberta.physics.cssdp.jaxb.MnemonicAdapter;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.JSONMnemonicDeserializer;
import ca.ualberta.physics.cssdp.util.JSONMnemonicSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@Api(value = "The search request", description = "Less values expands the search")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogueSearchRequest {

	@JsonSerialize(using = JSONMnemonicSerializer.class)
	@JsonDeserialize(using = JSONMnemonicDeserializer.class)
	@ApiModelProperty(value = "The project key", dataType = "Mnemonic")
	@XmlElement(name = "project")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private Mnemonic projectKey;

	@JsonInclude(Include.NON_EMPTY)
	@ApiModelProperty(value = "The observatory keys to search", dataType = "Mnemonic")
	@XmlElementWrapper(name = "observatories")
	@XmlElement(name = "observatory")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private List<Mnemonic> observatoryKeys = new ArrayList<Mnemonic>();

	@JsonInclude(Include.NON_EMPTY)
	@ApiModelProperty(value = "The instrument types to search", dataType = "Mnemonic")
	@XmlElementWrapper(name = "instrumentTypes")
	@XmlElement(name = "instrumentType")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private List<Mnemonic> instrumentTypeKeys = new ArrayList<Mnemonic>();

	@ApiModelProperty(value = "The discriminator to search", dataType = "Mnemonic")
	@XmlElement(name = "discriminator")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private Mnemonic discriminatorKey;

	@JsonSerialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateTimeSerializer.class)
	@JsonDeserialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateTimeDeserializer.class)
	@ApiModelProperty(value = "The file start date range to include.  Date format follows ISO8601 YYYY-MM-DDThh:mm:ss.SSS", dataType = "String")
	@XmlElement
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime start;

	@JsonSerialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateTimeSerializer.class)
	@JsonDeserialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateTimeDeserializer.class)
	@ApiModelProperty(value = "The file end date range to include. Date format follows ISO8601 YYYY-MM-DDThh:mm:ss.SSS", dataType = "String")
	@XmlElement
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime end;

	@Override
	public String toString() {
		return Objects
				.toStringHelper(this)
				.add("project", projectKey)
				.add("observatories", Joiner.on("|").join(observatoryKeys))
				.add("instrument types", Joiner.on("|").join(instrumentTypeKeys))
				.add("discriminator", discriminatorKey).add("start", start)
				.add("end", end).toString();
	}

	public Mnemonic getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(Mnemonic projectExtKey) {
		this.projectKey = projectExtKey;
	}

	public List<Mnemonic> getObservatoryKeys() {
		return observatoryKeys;
	}

	public void setObservatoryKeys(List<Mnemonic> observatoryExtKeys) {
		this.observatoryKeys = observatoryExtKeys;
	}

	public List<Mnemonic> getInstrumentTypeKeys() {
		return instrumentTypeKeys;
	}

	public void setInstrumentTypeKeys(List<Mnemonic> instrumentTypeExtKeys) {
		this.instrumentTypeKeys = instrumentTypeExtKeys;
	}

	public Mnemonic getDiscriminatorKey() {
		return discriminatorKey;
	}

	public void setDiscriminatorKey(Mnemonic discriminatorExtKey) {
		this.discriminatorKey = discriminatorExtKey;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

}
