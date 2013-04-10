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
