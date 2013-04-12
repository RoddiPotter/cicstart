/* ============================================================
 * LocalDateAdapter.java
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
package ca.ualberta.physics.cssdp.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	public LocalDate unmarshal(String v) throws Exception {
		return LocalDate.parse(v, DateTimeFormat.forPattern("yyyy-MM-dd"));
	}

	public String marshal(LocalDate v) throws Exception {
		return v.toString("yyyy-MM-dd");
	}

}