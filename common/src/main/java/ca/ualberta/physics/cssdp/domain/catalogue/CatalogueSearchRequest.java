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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiClass;
import com.wordnik.swagger.annotations.ApiProperty;

@ApiClass(value = "The search request", description = "Less values expands the search")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogueSearchRequest {

	@ApiProperty(value = "The project key", dataType = "Mnemonic")
	@XmlElement(name = "project")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private Mnemonic projectExtKey;

	@ApiProperty(value = "The observatory keys to search", dataType = "Mnemonic")
	@XmlElementWrapper(name = "observatories")
	@XmlElement(name = "observatory")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private List<Mnemonic> observatoryExtKeys = new ArrayList<Mnemonic>();

	@ApiProperty(value = "The instrument types to search", dataType = "Mnemonic")
	@XmlElementWrapper(name = "instrumentTypes")
	@XmlElement(name = "instrumentType")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private List<Mnemonic> instrumentTypeExtKeys = new ArrayList<Mnemonic>();

	@ApiProperty(value = "The discriminator to search", dataType = "Mnemonic")
	@XmlElement(name = "discriminator")
	@XmlJavaTypeAdapter(MnemonicAdapter.class)
	private Mnemonic discriminatorExtKey;

	@JsonSerialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateSerializer.class)
	@JsonDeserialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateDeserializer.class)
	@ApiProperty(value = "The file start date range to include.  Date format follows ISO8601 YYYY-MM-DDThh:mm:ss.SSS", dataType = "String")
	@XmlElement
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime start;

	@JsonSerialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateSerializer.class)
	@JsonDeserialize(using = ca.ualberta.physics.cssdp.util.JSONLocalDateDeserializer.class)
	@ApiProperty(value = "The file end date range to include. Date format follows ISO8601 YYYY-MM-DDThh:mm:ss.SSS", dataType = "String")
	@XmlElement
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime end;

	public Mnemonic getProjectExtKey() {
		return projectExtKey;
	}

	public void setProjectExtKey(Mnemonic projectExtKey) {
		this.projectExtKey = projectExtKey;
	}

	public List<Mnemonic> getObservatoryExtKeys() {
		return observatoryExtKeys;
	}

	public void setObservatoryExtKeys(List<Mnemonic> observatoryExtKeys) {
		this.observatoryExtKeys = observatoryExtKeys;
	}

	public List<Mnemonic> getInstrumentTypeExtKeys() {
		return instrumentTypeExtKeys;
	}

	public void setInstrumentTypeExtKeys(List<Mnemonic> instrumentTypeExtKeys) {
		this.instrumentTypeExtKeys = instrumentTypeExtKeys;
	}

	public Mnemonic getDiscriminatorExtKey() {
		return discriminatorExtKey;
	}

	public void setDiscriminatorExtKey(Mnemonic discriminatorExtKey) {
		this.discriminatorExtKey = discriminatorExtKey;
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
