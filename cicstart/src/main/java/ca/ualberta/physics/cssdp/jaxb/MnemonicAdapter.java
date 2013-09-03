/* ============================================================
 * MnemonicAdapter.java
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

import ca.ualberta.physics.cssdp.model.Mnemonic;

import com.google.common.base.Strings;

/**
 * Marshals Mnemonics back and forth between String and Mnemonic
 * representations.
 */
public class MnemonicAdapter extends XmlAdapter<String, Mnemonic> {

	@Override
	public String marshal(Mnemonic m) throws Exception {
		if (m != null) {
			return m.getValue();
		}
		return "";
	}

	@Override
	public Mnemonic unmarshal(String value) throws Exception {
		if (!Strings.isNullOrEmpty(value)) {
			return new Mnemonic(value);
		}
		return null;
	}

}
